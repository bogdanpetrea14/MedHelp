import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Paper, Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Button, IconButton, Dialog, DialogActions,
    DialogContent, DialogContentText, DialogTitle, Box, TextField, Chip, Alert,
    TablePagination, MenuItem
} from '@mui/material';
import { Delete, Add, Edit, PersonSearch } from '@mui/icons-material';
import api from '../api/axios';

const SEVERITIES = ['MILD', 'MODERATE', 'SEVERE'];

const AllergiesPage = () => {
    const [allergies, setAllergies] = useState([]);
    const [substances, setSubstances] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [openDelete, setOpenDelete] = useState(false);
    const [openAdd, setOpenAdd] = useState(false);
    const [openEdit, setOpenEdit] = useState(false);

    const [selectedId, setSelectedId] = useState(null);
    const [editAllergy, setEditAllergy] = useState(null);

    const [newAllergy, setNewAllergy] = useState({ activeSubstanceId: '', severity: 'MILD', notes: '' });

    // Search + pagination
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

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
            alert("Eroare la salvare: " + (err.response?.data?.message || "Verifică datele"));
        }
    };

    const handleOpenEdit = (allergy) => {
        setEditAllergy({ id: allergy.id, severity: allergy.severity, notes: allergy.notes || '' });
        setOpenEdit(true);
    };

    const handleSaveEdit = async () => {
        try {
            await api.put(`/allergies/${editAllergy.id}`, {
                activeSubstanceId: allergies.find(a => a.id === editAllergy.id)?.activeSubstanceId,
                severity: editAllergy.severity,
                notes: editAllergy.notes,
            });
            setOpenEdit(false);
            fetchAllergies();
        } catch (err) {
            alert("Eroare la actualizare: " + (err.response?.data?.message || "Verifică datele"));
        }
    };

    // Filtrare
    const filtered = allergies.filter(a => {
        const term = searchTerm.toLowerCase();
        return (
            (a.activeSubstanceName || '').toLowerCase().includes(term) ||
            (a.severity || '').toLowerCase().includes(term) ||
            (a.patientName || '').toLowerCase().includes(term)
        );
    });

    const paginated = filtered.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
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

            {/* Search */}
            <Paper sx={{ p: 2, mb: 2, borderRadius: 2 }}>
                <TextField
                    label="Caută după substanță, severitate sau pacient..."
                    variant="outlined"
                    fullWidth
                    value={searchTerm}
                    onChange={(e) => { setSearchTerm(e.target.value); setPage(0); }}
                />
            </Paper>

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
                        {paginated.length > 0 ? (
                            paginated.map((row) => (
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
                                        <IconButton color="primary" onClick={() => handleOpenEdit(row)}>
                                            <Edit />
                                        </IconButton>
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
                <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={filtered.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={(_, p) => setPage(p)}
                    onRowsPerPageChange={(e) => { setRowsPerPage(parseInt(e.target.value, 10)); setPage(0); }}
                    labelRowsPerPage="Rânduri pe pagină:"
                />
            </TableContainer>

            {/* Dialog Adaugare */}
            {userRole !== 'ADMIN' && (
                <Dialog open={openAdd} onClose={() => setOpenAdd(false)} fullWidth maxWidth="xs">
                    <DialogTitle sx={{ fontWeight: 'bold' }}>Adaugă Alergie Nouă</DialogTitle>
                    <DialogContent>
                        <TextField
                            select fullWidth margin="normal" label="Substanță Activă"
                            value={newAllergy.activeSubstanceId}
                            onChange={(e) => setNewAllergy({ ...newAllergy, activeSubstanceId: e.target.value })}
                        >
                            <MenuItem value="">-- Selectează substanța --</MenuItem>
                            {substances.map(s => (
                                <MenuItem key={s.id} value={s.id}>{s.name} ({s.category})</MenuItem>
                            ))}
                        </TextField>
                        <TextField
                            select fullWidth margin="normal" label="Severitate"
                            value={newAllergy.severity}
                            onChange={(e) => setNewAllergy({ ...newAllergy, severity: e.target.value })}
                        >
                            {SEVERITIES.map(s => <MenuItem key={s} value={s}>{s}</MenuItem>)}
                        </TextField>
                        <TextField
                            label="Observații" fullWidth margin="normal" multiline rows={2}
                            value={newAllergy.notes}
                            onChange={(e) => setNewAllergy({ ...newAllergy, notes: e.target.value })}
                        />
                    </DialogContent>
                    <DialogActions sx={{ p: 2 }}>
                        <Button onClick={() => setOpenAdd(false)}>Anulează</Button>
                        <Button onClick={handleSaveAllergy} variant="contained">Salvează</Button>
                    </DialogActions>
                </Dialog>
            )}

            {/* Dialog Editare */}
            <Dialog open={openEdit} onClose={() => setOpenEdit(false)} fullWidth maxWidth="xs">
                <DialogTitle sx={{ fontWeight: 'bold' }}>Editează Alergie</DialogTitle>
                <DialogContent>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1, mb: 1 }}>
                        Substanță: <strong>{allergies.find(a => a.id === editAllergy?.id)?.activeSubstanceName}</strong>
                    </Typography>
                    <TextField
                        select fullWidth margin="normal" label="Severitate"
                        value={editAllergy?.severity || 'MILD'}
                        onChange={(e) => setEditAllergy({ ...editAllergy, severity: e.target.value })}
                    >
                        {SEVERITIES.map(s => <MenuItem key={s} value={s}>{s}</MenuItem>)}
                    </TextField>
                    <TextField
                        label="Observații" fullWidth margin="normal" multiline rows={2}
                        value={editAllergy?.notes || ''}
                        onChange={(e) => setEditAllergy({ ...editAllergy, notes: e.target.value })}
                    />
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button onClick={() => setOpenEdit(false)}>Anulează</Button>
                    <Button onClick={handleSaveEdit} variant="contained">Salvează</Button>
                </DialogActions>
            </Dialog>

            {/* Dialog Stergere */}
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