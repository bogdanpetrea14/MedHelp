import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Paper, Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Button, IconButton, Dialog, DialogActions,
    DialogContent, DialogContentText, DialogTitle, Box, TextField, Chip, Alert
} from '@mui/material';
import { Delete, Add, PersonSearch } from '@mui/icons-material';
import api from '../api/axios';

const AllergiesPage = () => {
    const [allergies, setAllergies] = useState([]);
    const [substances, setSubstances] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [openDelete, setOpenDelete] = useState(false);
    const [openAdd, setOpenAdd] = useState(false);
    const [selectedId, setSelectedId] = useState(null);

    const [newAllergy, setNewAllergy] = useState({ activeSubstanceId: '', severity: 'MILD', notes: '' });

    const userRole = localStorage.getItem('role');

    useEffect(() => {
        fetchAllergies();
        api.get('/active-substances')
            .then(res => setSubstances(res.data))
            .catch(err => console.error('Eroare la încărcare substanțe:', err));
    }, []);

    const fetchAllergies = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await api.get('/allergies');
            setAllergies(response.data);
        } catch (err) {
            console.error(err);
            setError('Eroare la încărcare: Sesiune expirată sau permisiuni insuficiente.');
        } finally {
            setLoading(false);
        }
    };

    const handleConfirmDelete = async () => {
        try {
            await api.delete(`/allergies/${selectedId}`);
            setOpenDelete(false);
            fetchAllergies();
        } catch (err) {
            alert("Nu s-a putut șterge: " + (err.response?.data?.message || "Eroare server"));
        }
    };

    const handleSaveAllergy = async () => {
        if (!newAllergy.activeSubstanceId) {
            alert("Selectează o substanță activă!");
            return;
        }
        try {
            await api.post('/allergies', newAllergy);
            setOpenAdd(false);
            setNewAllergy({ activeSubstanceId: '', severity: 'MILD', notes: '' });
            fetchAllergies();
        } catch (err) {
            alert("Eroare la salvare: " + (err.response?.data || "Verifică datele"));
        }
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <PersonSearch color="primary" sx={{ fontSize: 40 }} />
                    <Typography variant="h4" fontWeight="bold">
                        {userRole === 'ADMIN' ? 'Gestiune Alergii (Mod Admin)' : 'Alergiile Mele'}
                    </Typography>
                </Box>
                {userRole !== 'ADMIN' && (
                    <Button variant="contained" startIcon={<Add />} onClick={() => setOpenAdd(true)} size="large">
                        Adaugă Alergie
                    </Button>
                )}
            </Box>

            {error && <Alert severity="warning" sx={{ mb: 2 }}>{error}</Alert>}

            <TableContainer component={Paper} elevation={4} sx={{ borderRadius: 3, overflow: 'hidden' }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#1976d2' }}>
                        <TableRow>
                            {userRole === 'ADMIN' && <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Pacient</TableCell>}
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Substanță Activă</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Severitate</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Note</TableCell>
                            <TableCell align="right" sx={{ color: 'white', fontWeight: 'bold' }}>Acțiuni</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {allergies.length > 0 ? (
                            allergies.map((row) => (
                                <TableRow key={row.id} hover>
                                    {userRole === 'ADMIN' && (
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {row.patientName || 'Nespecificat'}
                                        </TableCell>
                                    )}
                                    <TableCell>{row.activeSubstanceName || 'Substanță necunoscută'}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={row.severity}
                                            color={row.severity === 'SEVERE' ? 'error' : row.severity === 'MODERATE' ? 'warning' : 'info'}
                                            variant="filled"
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>{row.notes || '-'}</TableCell>
                                    <TableCell align="right">
                                        <IconButton
                                            color="error"
                                            onClick={() => { setSelectedId(row.id); setOpenDelete(true); }}
                                        >
                                            <Delete />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={userRole === 'ADMIN' ? 5 : 4} align="center" sx={{ py: 4 }}>
                                    {loading ? 'Se încarcă...' : 'Nu există alergii înregistrate.'}
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* MODALA ADAUGARE — vizibila doar pentru PATIENT si DOCTOR */}
            {userRole !== 'ADMIN' && (
                <Dialog open={openAdd} onClose={() => setOpenAdd(false)} fullWidth maxWidth="xs">
                    <DialogTitle sx={{ fontWeight: 'bold' }}>Adaugă Alergie Nouă</DialogTitle>
                    <DialogContent>
                        <TextField
                            select fullWidth margin="normal" label="Substanță Activă"
                            SelectProps={{ native: true }}
                            value={newAllergy.activeSubstanceId}
                            onChange={(e) => setNewAllergy({...newAllergy, activeSubstanceId: e.target.value})}
                        >
                            <option value="">-- Selectează substanța --</option>
                            {substances.map(s => (
                                <option key={s.id} value={s.id}>{s.name} ({s.category})</option>
                            ))}
                        </TextField>
                        <TextField
                            select fullWidth margin="normal" label="Severitate"
                            SelectProps={{ native: true }}
                            value={newAllergy.severity}
                            onChange={(e) => setNewAllergy({...newAllergy, severity: e.target.value})}
                        >
                            <option value="MILD">MILD</option>
                            <option value="MODERATE">MODERATE</option>
                            <option value="SEVERE">SEVERE</option>
                        </TextField>
                        <TextField
                            label="Observații" fullWidth margin="normal" multiline rows={2}
                            value={newAllergy.notes}
                            onChange={(e) => setNewAllergy({...newAllergy, notes: e.target.value})}
                        />
                    </DialogContent>
                    <DialogActions sx={{ p: 2 }}>
                        <Button onClick={() => setOpenAdd(false)}>Anulează</Button>
                        <Button onClick={handleSaveAllergy} variant="contained" color="primary">Salvează</Button>
                    </DialogActions>
                </Dialog>
            )}

            {/* MODALA CONFIRMARE STERGERE */}
            <Dialog open={openDelete} onClose={() => setOpenDelete(false)}>
                <DialogTitle>Confirmi ștergerea?</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Această acțiune va elimina permanent alergia din dosarul pacientului.
                    </DialogContentText>
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button onClick={() => setOpenDelete(false)}>Nu, revino</Button>
                    <Button onClick={handleConfirmDelete} color="error" variant="contained">Da, șterge</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default AllergiesPage;