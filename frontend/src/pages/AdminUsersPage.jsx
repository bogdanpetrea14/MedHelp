import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Box, Tabs, Tab, Paper, Table, TableBody,
    TableCell, TableContainer, TableHead, TableRow, IconButton, Chip,
    Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions,
    Button, Alert, CircularProgress, Tooltip
} from '@mui/material';
import {
    LockReset, Block, CheckCircle, DeleteForever, AdminPanelSettings
} from '@mui/icons-material';
import api from '../api/axios';

const STATUS_COLOR = {
    ACTIVE: 'success',
    PENDING: 'warning',
    INACTIVE: 'default',
    REJECTED: 'error',
};

const STATUS_LABEL = {
    ACTIVE: 'Activ',
    PENDING: 'În așteptare',
    INACTIVE: 'Suspendat',
    REJECTED: 'Respins',
};

// Dialog pentru afișarea parolei temporare
const TempPasswordDialog = ({ open, password, onClose }) => (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
        <DialogTitle fontWeight="bold" color="warning.dark">Parolă Temporară Generată</DialogTitle>
        <DialogContent>
            <DialogContentText gutterBottom>
                Comunică această parolă temporară utilizatorului. Ea nu va mai putea fi vizualizată ulterior.
            </DialogContentText>
            <Box sx={{ mt: 2, p: 2, bgcolor: '#f5f5f5', borderRadius: 2, textAlign: 'center' }}>
                <Typography variant="h5" fontWeight="bold" fontFamily="monospace" letterSpacing={3}>
                    {password}
                </Typography>
            </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
            <Button onClick={onClose} variant="contained">Am notat parola</Button>
        </DialogActions>
    </Dialog>
);

// Dialog confirmare ștergere
const DeleteDialog = ({ open, onClose, onConfirm }) => (
    <Dialog open={open} onClose={onClose}>
        <DialogTitle fontWeight="bold" color="error">Confirmi ștergerea definitivă?</DialogTitle>
        <DialogContent>
            <DialogContentText>
                Contul și toate datele de profil asociate vor fi șterse permanent.
                Dacă utilizatorul are rețete, ștergerea va fi blocată — folosiți inactivarea în schimb.
            </DialogContentText>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
            <Button onClick={onClose}>Anulează</Button>
            <Button onClick={onConfirm} color="error" variant="contained">Șterge Definitiv</Button>
        </DialogActions>
    </Dialog>
);

// Butoane acțiuni comune
const ActionButtons = ({ row, onResetPassword, onToggleStatus, onDelete }) => {
    const isActive = row.status === 'ACTIVE';
    return (
        <Box sx={{ display: 'flex', gap: 0.5, justifyContent: 'flex-end' }}>
            <Tooltip title="Resetare forțată parolă">
                <IconButton size="small" color="warning" onClick={() => onResetPassword(row.userId)}>
                    <LockReset />
                </IconButton>
            </Tooltip>
            <Tooltip title={isActive ? 'Suspendă contul' : 'Activează contul'}>
                <IconButton
                    size="small"
                    color={isActive ? 'default' : 'success'}
                    onClick={() => onToggleStatus(row.userId, isActive)}
                >
                    {isActive ? <Block /> : <CheckCircle />}
                </IconButton>
            </Tooltip>
            <Tooltip title="Ștergere definitivă">
                <IconButton size="small" color="error" onClick={() => onDelete(row.userId)}>
                    <DeleteForever />
                </IconButton>
            </Tooltip>
        </Box>
    );
};

const AdminUsersPage = () => {
    const [tab, setTab] = useState(0);
    const [patients, setPatients] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [pharmacies, setPharmacies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Dialog stare
    const [tempPassword, setTempPassword] = useState('');
    const [showTempDialog, setShowTempDialog] = useState(false);
    const [deleteUserId, setDeleteUserId] = useState(null);
    const [showDeleteDialog, setShowDeleteDialog] = useState(false);

    useEffect(() => {
        fetchAll();
    }, []);

    const fetchAll = async () => {
        setLoading(true);
        setError('');
        try {
            const [p, d, ph] = await Promise.all([
                api.get('/admin/users/patients'),
                api.get('/admin/users/doctors'),
                api.get('/admin/users/pharmacies'),
            ]);
            setPatients(p.data);
            setDoctors(d.data);
            setPharmacies(ph.data);
        } catch (err) {
            setError('Eroare la încărcarea utilizatorilor.');
        } finally {
            setLoading(false);
        }
    };

    const handleResetPassword = async (userId) => {
        try {
            const res = await api.post(`/admin/users/${userId}/reset-password`);
            setTempPassword(res.data.temporaryPassword);
            setShowTempDialog(true);
        } catch (err) {
            setError(err.response?.data?.message || 'Eroare la resetarea parolei.');
        }
    };

    const handleToggleStatus = async (userId, isCurrentlyActive) => {
        try {
            if (isCurrentlyActive) {
                await api.patch(`/admin/users/${userId}/deactivate`);
                setSuccess('Contul a fost suspendat.');
            } else {
                await api.patch(`/admin/users/${userId}/activate`);
                setSuccess('Contul a fost activat.');
            }
            fetchAll();
        } catch (err) {
            setError(err.response?.data?.message || 'Eroare la modificarea statusului.');
        }
    };

    const handleDeleteConfirm = async () => {
        try {
            await api.delete(`/admin/users/${deleteUserId}`);
            setSuccess('Contul a fost șters definitiv.');
            setShowDeleteDialog(false);
            fetchAll();
        } catch (err) {
            setShowDeleteDialog(false);
            setError(err.response?.data?.message || 'Eroare la ștergere.');
        }
    };

    const commonProps = { onResetPassword: handleResetPassword, onToggleStatus: handleToggleStatus, onDelete: (id) => { setDeleteUserId(id); setShowDeleteDialog(true); } };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
                <CircularProgress size={48} />
            </Box>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 6 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                <AdminPanelSettings color="primary" sx={{ fontSize: 40 }} />
                <Box>
                    <Typography variant="h4" fontWeight="bold" color="primary">
                        Gestiune Utilizatori
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        Vizualizare și administrare conturi pe verticale de rol
                    </Typography>
                </Box>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

            <Paper sx={{ borderRadius: 2 }}>
                <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ borderBottom: 1, borderColor: 'divider', px: 2 }}>
                    <Tab label={`Pacienți (${patients.length})`} />
                    <Tab label={`Doctori (${doctors.length})`} />
                    <Tab label={`Farmacii (${pharmacies.length})`} />
                </Tabs>

                {/* ---- TAB PACIENȚI ---- */}
                {tab === 0 && (
                    <TableContainer>
                        <Table>
                            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                                <TableRow>
                                    <TableCell><strong>Nume</strong></TableCell>
                                    <TableCell><strong>Email</strong></TableCell>
                                    <TableCell><strong>CNP</strong></TableCell>
                                    <TableCell><strong>Data nașterii</strong></TableCell>
                                    <TableCell><strong>Rețete</strong></TableCell>
                                    <TableCell><strong>Status</strong></TableCell>
                                    <TableCell align="right"><strong>Acțiuni</strong></TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {patients.length > 0 ? patients.map(row => (
                                    <TableRow key={row.userId} hover>
                                        <TableCell>{row.firstName} {row.lastName}</TableCell>
                                        <TableCell>{row.email}</TableCell>
                                        <TableCell sx={{ fontFamily: 'monospace' }}>{row.cnp}</TableCell>
                                        <TableCell>{row.birthDate || '—'}</TableCell>
                                        <TableCell>{row.prescriptionCount}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={STATUS_LABEL[row.status] || row.status}
                                                color={STATUS_COLOR[row.status] || 'default'}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <ActionButtons row={row} {...commonProps} />
                                        </TableCell>
                                    </TableRow>
                                )) : (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                                            Nu există pacienți înregistrați.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                {/* ---- TAB DOCTORI ---- */}
                {tab === 1 && (
                    <TableContainer>
                        <Table>
                            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                                <TableRow>
                                    <TableCell><strong>Nume</strong></TableCell>
                                    <TableCell><strong>Email</strong></TableCell>
                                    <TableCell><strong>Cod parafă</strong></TableCell>
                                    <TableCell><strong>Specializare</strong></TableCell>
                                    <TableCell><strong>Unitate medicală</strong></TableCell>
                                    <TableCell><strong>Status</strong></TableCell>
                                    <TableCell align="right"><strong>Acțiuni</strong></TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {doctors.length > 0 ? doctors.map(row => (
                                    <TableRow key={row.userId} hover>
                                        <TableCell>Dr. {row.firstName} {row.lastName}</TableCell>
                                        <TableCell>{row.email}</TableCell>
                                        <TableCell sx={{ fontFamily: 'monospace' }}>{row.licenseNumber}</TableCell>
                                        <TableCell>{row.speciality}</TableCell>
                                        <TableCell>{row.medicalUnit || '—'}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={STATUS_LABEL[row.status] || row.status}
                                                color={STATUS_COLOR[row.status] || 'default'}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <ActionButtons row={row} {...commonProps} />
                                        </TableCell>
                                    </TableRow>
                                )) : (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                                            Nu există doctori înregistrați.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                {/* ---- TAB FARMACII ---- */}
                {tab === 2 && (
                    <TableContainer>
                        <Table>
                            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                                <TableRow>
                                    <TableCell><strong>Nume farmacie</strong></TableCell>
                                    <TableCell><strong>Email</strong></TableCell>
                                    <TableCell><strong>CUI</strong></TableCell>
                                    <TableCell><strong>Adresă sediu</strong></TableCell>
                                    <TableCell><strong>Licență funcționare</strong></TableCell>
                                    <TableCell><strong>Status</strong></TableCell>
                                    <TableCell align="right"><strong>Acțiuni</strong></TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {pharmacies.length > 0 ? pharmacies.map(row => (
                                    <TableRow key={row.userId} hover>
                                        <TableCell fontWeight="bold">{row.name}</TableCell>
                                        <TableCell>{row.email}</TableCell>
                                        <TableCell sx={{ fontFamily: 'monospace' }}>{row.cui || '—'}</TableCell>
                                        <TableCell>{row.address}</TableCell>
                                        <TableCell>{row.pharmacyLicense || '—'}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={STATUS_LABEL[row.status] || row.status}
                                                color={STATUS_COLOR[row.status] || 'default'}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <ActionButtons row={row} {...commonProps} />
                                        </TableCell>
                                    </TableRow>
                                )) : (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                                            Nu există farmacii înregistrate.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </Paper>

            <TempPasswordDialog
                open={showTempDialog}
                password={tempPassword}
                onClose={() => setShowTempDialog(false)}
            />
            <DeleteDialog
                open={showDeleteDialog}
                onClose={() => setShowDeleteDialog(false)}
                onConfirm={handleDeleteConfirm}
            />
        </Container>
    );
};

export default AdminUsersPage;