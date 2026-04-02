import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Box, Tabs, Tab, Paper, Table, TableBody,
    TableCell, TableContainer, TableHead, TableRow, Chip, Button,
    Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions,
    TextField, Alert, CircularProgress, Tooltip
} from '@mui/material';
import { CheckCircle, Cancel, HowToReg } from '@mui/icons-material';
import api from '../api/axios';

const RejectDialog = ({ open, onClose, onConfirm }) => {
    const [reason, setReason] = useState('');

    const handleConfirm = () => {
        onConfirm(reason.trim() || null);
        setReason('');
    };

    const handleClose = () => {
        setReason('');
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <DialogTitle fontWeight="bold" color="error">Respinge cererea</DialogTitle>
            <DialogContent>
                <DialogContentText gutterBottom>
                    Contul va fi marcat ca <strong>Respins</strong> și utilizatorul nu va putea accesa platforma.
                </DialogContentText>
                <TextField
                    label="Motiv respingere (opțional)"
                    fullWidth
                    multiline
                    rows={3}
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    placeholder="Ex: Documentele furnizate sunt incomplete sau invalide."
                    sx={{ mt: 2 }}
                />
            </DialogContent>
            <DialogActions sx={{ p: 2 }}>
                <Button onClick={handleClose}>Anulează</Button>
                <Button onClick={handleConfirm} color="error" variant="contained">
                    Respinge Cererea
                </Button>
            </DialogActions>
        </Dialog>
    );
};

const AdminApprovalsPage = () => {
    const [tab, setTab] = useState(0);
    const [doctors, setDoctors] = useState([]);
    const [pharmacies, setPharmacies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [rejectTarget, setRejectTarget] = useState(null);
    const [showRejectDialog, setShowRejectDialog] = useState(false);

    const fetchPending = async () => {
        setLoading(true);
        setError('');
        try {
            const [d, ph] = await Promise.all([
                api.get('/admin/users/pending/doctors'),
                api.get('/admin/users/pending/pharmacies'),
            ]);
            setDoctors(d.data);
            setPharmacies(ph.data);
        } catch {
            setError('Eroare la încărcarea cererilor în așteptare.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchPending(); }, []);

    const handleApprove = async (userId) => {
        try {
            await api.post(`/admin/users/${userId}/approve`);
            setSuccess('Contul a fost aprobat și activat.');
            fetchPending();
        } catch (err) {
            setError(err.response?.data?.message || 'Eroare la aprobare.');
        }
    };

    const handleRejectConfirm = async (reason) => {
        try {
            await api.post(`/admin/users/${rejectTarget}/reject`, { reason });
            setSuccess('Cererea a fost respinsă.');
            setShowRejectDialog(false);
            setRejectTarget(null);
            fetchPending();
        } catch (err) {
            setShowRejectDialog(false);
            setError(err.response?.data?.message || 'Eroare la respingere.');
        }
    };

    const openReject = (userId) => {
        setRejectTarget(userId);
        setShowRejectDialog(true);
    };

    const formatDate = (dt) => dt ? new Date(dt).toLocaleDateString('ro-RO') : '—';

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}><CircularProgress size={48} /></Box>;
    }

    const total = doctors.length + pharmacies.length;

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 6 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                <HowToReg color="primary" sx={{ fontSize: 40 }} />
                <Box>
                    <Typography variant="h4" fontWeight="bold" color="primary">
                        Aprobări Conturi
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {total === 0
                            ? 'Nu există cereri de aprobare în așteptare.'
                            : `${total} cerere${total !== 1 ? 'i' : ''} de aprobare în așteptare`}
                    </Typography>
                </Box>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

            <Paper sx={{ borderRadius: 2 }}>
                <Tabs
                    value={tab}
                    onChange={(_, v) => setTab(v)}
                    sx={{ borderBottom: 1, borderColor: 'divider', px: 2 }}
                >
                    <Tab label={`Doctori (${doctors.length})`} />
                    <Tab label={`Farmacii (${pharmacies.length})`} />
                </Tabs>

                {/* ---- TAB DOCTORI ---- */}
                {tab === 0 && (
                    <TableContainer>
                        <Table>
                            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                                <TableRow>
                                    <TableCell><strong>Nume</strong></TableCell>
                                    <TableCell><strong>Email</strong></TableCell>
                                    <TableCell><strong>Cod parafă</strong></TableCell>
                                    <TableCell><strong>Specializare</strong></TableCell>
                                    <TableCell><strong>Unitate medicală</strong></TableCell>
                                    <TableCell><strong>Data înregistrării</strong></TableCell>
                                    <TableCell align="center"><strong>Decizie</strong></TableCell>
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
                                        <TableCell>{formatDate(row.registeredAt)}</TableCell>
                                        <TableCell>
                                            <Box sx={{ display: 'flex', gap: 1, justifyContent: 'center' }}>
                                                <Tooltip title="Aprobă contul">
                                                    <Button
                                                        size="small"
                                                        variant="contained"
                                                        color="success"
                                                        startIcon={<CheckCircle />}
                                                        onClick={() => handleApprove(row.userId)}
                                                        sx={{ textTransform: 'none' }}
                                                    >
                                                        Aprobă
                                                    </Button>
                                                </Tooltip>
                                                <Tooltip title="Respinge cererea">
                                                    <Button
                                                        size="small"
                                                        variant="outlined"
                                                        color="error"
                                                        startIcon={<Cancel />}
                                                        onClick={() => openReject(row.userId)}
                                                        sx={{ textTransform: 'none' }}
                                                    >
                                                        Respinge
                                                    </Button>
                                                </Tooltip>
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                )) : (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                                            Nu există doctori în așteptarea aprobării.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                {/* ---- TAB FARMACII ---- */}
                {tab === 1 && (
                    <TableContainer>
                        <Table>
                            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                                <TableRow>
                                    <TableCell><strong>Nume farmacie</strong></TableCell>
                                    <TableCell><strong>Email</strong></TableCell>
                                    <TableCell><strong>CUI</strong></TableCell>
                                    <TableCell><strong>Adresă sediu</strong></TableCell>
                                    <TableCell><strong>Licență funcționare</strong></TableCell>
                                    <TableCell><strong>Data înregistrării</strong></TableCell>
                                    <TableCell align="center"><strong>Decizie</strong></TableCell>
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
                                        <TableCell>{formatDate(row.registeredAt)}</TableCell>
                                        <TableCell>
                                            <Box sx={{ display: 'flex', gap: 1, justifyContent: 'center' }}>
                                                <Tooltip title="Aprobă contul">
                                                    <Button
                                                        size="small"
                                                        variant="contained"
                                                        color="success"
                                                        startIcon={<CheckCircle />}
                                                        onClick={() => handleApprove(row.userId)}
                                                        sx={{ textTransform: 'none' }}
                                                    >
                                                        Aprobă
                                                    </Button>
                                                </Tooltip>
                                                <Tooltip title="Respinge cererea">
                                                    <Button
                                                        size="small"
                                                        variant="outlined"
                                                        color="error"
                                                        startIcon={<Cancel />}
                                                        onClick={() => openReject(row.userId)}
                                                        sx={{ textTransform: 'none' }}
                                                    >
                                                        Respinge
                                                    </Button>
                                                </Tooltip>
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                )) : (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                                            Nu există farmacii în așteptarea aprobării.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </Paper>

            <RejectDialog
                open={showRejectDialog}
                onClose={() => { setShowRejectDialog(false); setRejectTarget(null); }}
                onConfirm={handleRejectConfirm}
            />
        </Container>
    );
};

export default AdminApprovalsPage;