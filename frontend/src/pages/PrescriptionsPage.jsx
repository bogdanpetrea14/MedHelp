import React, { useState, useEffect, useMemo } from 'react';
import {
    Container, Typography, Paper, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, TextField, TablePagination,
    CircularProgress, Box, Chip, Button, IconButton, Dialog, DialogTitle,
    DialogContent, DialogContentText, DialogActions, MenuItem, Alert, Collapse
} from '@mui/material';
import { Add, Edit, Delete, ExpandMore, ExpandLess, Science, Cancel } from '@mui/icons-material';
import api from '../api/axios';

const STATUS_COLORS = {
    FULFILLED: 'success',
    PRESCRIBED: 'primary',
    CANCELLED: 'error',
    PARTIALLY_FULFILLED: 'warning',
};

const EMPTY_ITEM = { activeSubstanceId: '', dose: '', frequency: '', durationDays: '', notes: '' };

// Sub-tabel cu medicamentele unei rețete
const MedicationSubRow = ({ items, colSpan }) => (
    <TableRow>
        <TableCell colSpan={colSpan} sx={{ py: 0, bgcolor: '#fafafa' }}>
            <Collapse in timeout="auto" unmountOnExit>
                <Box sx={{ py: 2, px: 3 }}>
                    <Typography variant="subtitle2" fontWeight="bold" sx={{ mb: 1, display: 'flex', alignItems: 'center', gap: 0.5 }}>
                        <Science fontSize="small" color="primary" /> Medicamente prescrise
                    </Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow sx={{ bgcolor: '#e3f2fd' }}>
                                <TableCell><strong>Substanță Activă</strong></TableCell>
                                <TableCell><strong>Doză</strong></TableCell>
                                <TableCell><strong>Frecvență</strong></TableCell>
                                <TableCell><strong>Durată</strong></TableCell>
                                <TableCell><strong>Note</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {items.map((item, idx) => (
                                <TableRow key={item.id || idx}>
                                    <TableCell>{item.activeSubstance?.name || '—'}</TableCell>
                                    <TableCell>{item.dose}</TableCell>
                                    <TableCell>{item.frequency}</TableCell>
                                    <TableCell>{item.durationDays} zile</TableCell>
                                    <TableCell>{item.notes || '—'}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Box>
            </Collapse>
        </TableCell>
    </TableRow>
);

// Rând individual cu expand
const PrescriptionRow = ({ row, role, onEdit, onDelete, onCancel }) => {
    const [expanded, setExpanded] = useState(false);
    const isPatient = role === 'PATIENT';
    const isDoctor = role === 'DOCTOR';
    const isAdmin = role === 'ADMIN';
    const hasItems = row.items && row.items.length > 0;
    const isCancellable = row.status !== 'CANCELLED' && row.status !== 'FULFILLED';

    const colSpan = isAdmin ? 6 : 5;

    return (
        <>
            <TableRow hover>
                <TableCell sx={{ fontWeight: 'medium', color: '#1a237e' }}>{row.uniqueCode}</TableCell>

                {(isPatient || isAdmin) && (
                    <TableCell>
                        {row.doctor ? `Dr. ${row.doctor.firstName} ${row.doctor.lastName}` : 'N/A'}
                    </TableCell>
                )}
                {(isDoctor || isAdmin) && (
                    <TableCell>
                        {row.patient ? `${row.patient.firstName} ${row.patient.lastName}` : 'N/A'}
                    </TableCell>
                )}

                <TableCell>
                    <Chip
                        label={row.status}
                        color={STATUS_COLORS[row.status] || 'default'}
                        size="small"
                        variant="outlined"
                        sx={{ fontWeight: 'bold' }}
                    />
                </TableCell>

                <TableCell>
                    {row.prescribedAt ? new Date(row.prescribedAt).toLocaleDateString('ro-RO') : 'N/A'}
                </TableCell>

                <TableCell align="right">
                    {hasItems && (
                        <IconButton size="small" onClick={() => setExpanded(!expanded)} title="Medicamente">
                            {expanded ? <ExpandLess /> : <ExpandMore />}
                        </IconButton>
                    )}
                    {/* DOCTOR: buton Anulează (cu motiv) */}
                    {isDoctor && (
                        <IconButton
                            color="warning"
                            size="small"
                            onClick={() => onCancel(row)}
                            disabled={!isCancellable}
                            title={isCancellable ? 'Anulează rețeta' : 'Rețeta nu poate fi anulată'}
                        >
                            <Cancel />
                        </IconButton>
                    )}
                    {/* ADMIN: Edit + Delete + Anulează */}
                    {isAdmin && (
                        <>
                            <IconButton color="primary" size="small" onClick={() => onEdit(row)}>
                                <Edit />
                            </IconButton>
                            <IconButton
                                color="warning"
                                size="small"
                                onClick={() => onCancel(row)}
                                disabled={!isCancellable}
                                title="Anulează rețeta"
                            >
                                <Cancel />
                            </IconButton>
                            <IconButton color="error" size="small" onClick={() => onDelete(row.id)}>
                                <Delete />
                            </IconButton>
                        </>
                    )}
                </TableCell>
            </TableRow>

            {expanded && hasItems && (
                <MedicationSubRow items={row.items} colSpan={colSpan} />
            )}
        </>
    );
};

const PrescriptionsPage = () => {
    const role = localStorage.getItem('role');
    const isPatient = role === 'PATIENT';
    const isDoctor = role === 'DOCTOR';
    const isAdmin = role === 'ADMIN';

    const [prescriptions, setPrescriptions] = useState([]);
    const [patients, setPatients] = useState([]);        // pentru Add dialog
    const [substances, setSubstances] = useState([]);    // pentru Add dialog
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [searchTerm, setSearchTerm] = useState('');
    const [patientFilter, setPatientFilter] = useState(''); // filtru per pacient (doctor)
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    const [openAdd, setOpenAdd] = useState(false);
    const [openEdit, setOpenEdit] = useState(false);
    const [openDelete, setOpenDelete] = useState(false);
    const [openCancel, setOpenCancel] = useState(false);
    const [cancelReason, setCancelReason] = useState('');
    const [selectedId, setSelectedId] = useState(null);

    const [newPrescription, setNewPrescription] = useState({
        patientId: '', doctorNotes: '', items: [{ ...EMPTY_ITEM }],
    });
    const [editData, setEditData] = useState({ doctorNotes: '', status: '' });

    useEffect(() => {
        fetchPrescriptions();
        if (isDoctor) {
            api.get('/patients/my-patients').then(r => setPatients(r.data)).catch(console.error);
            api.get('/active-substances').then(r => setSubstances(r.data)).catch(console.error);
        }
    }, []);

    const fetchPrescriptions = async () => {
        setLoading(true);
        setError('');
        try {
            let endpoint = '/prescriptions/my-prescriptions';
            if (isAdmin) endpoint = '/prescriptions/all';
            if (isDoctor) endpoint = '/prescriptions/my-doctor-prescriptions';
            const response = await api.get(endpoint);
            setPrescriptions(response.data);
        } catch (err) {
            setError('Eroare la încărcarea rețetelor.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Pacienți unici din rețetele doctorului (pentru filtrul dropdown)
    const uniquePatientsInPrescriptions = useMemo(() => {
        if (!isDoctor) return [];
        const seen = new Map();
        prescriptions.forEach(p => {
            if (p.patient && !seen.has(p.patient.id)) {
                seen.set(p.patient.id, p.patient);
            }
        });
        return Array.from(seen.values());
    }, [prescriptions, isDoctor]);

    // ---- CRUD handlers ----

    const handleAddPrescription = async () => {
        if (!newPrescription.patientId) { alert('Selectează un pacient!'); return; }
        const invalid = newPrescription.items.find(
            i => !i.activeSubstanceId || !i.dose || !i.frequency || !i.durationDays
        );
        if (invalid) { alert('Completează toate câmpurile pentru fiecare medicament!'); return; }
        try {
            await api.post('/prescriptions', {
                patientId: newPrescription.patientId,
                doctorNotes: newPrescription.doctorNotes,
                items: newPrescription.items.map(i => ({
                    activeSubstanceId: i.activeSubstanceId,
                    dose: i.dose,
                    frequency: i.frequency,
                    durationDays: parseInt(i.durationDays),
                    notes: i.notes,
                })),
            });
            setOpenAdd(false);
            setNewPrescription({ patientId: '', doctorNotes: '', items: [{ ...EMPTY_ITEM }] });
            fetchPrescriptions();
        } catch (err) {
            alert('Eroare: ' + (err.response?.data?.message || err.response?.data || 'Verifică datele'));
        }
    };

    const handleOpenEdit = (p) => {
        setSelectedId(p.id);
        setEditData({ doctorNotes: p.doctorNotes || '', status: p.status });
        setOpenEdit(true);
    };

    const handleSaveEdit = async () => {
        try {
            await api.put(`/prescriptions/${selectedId}`, editData);
            setOpenEdit(false);
            fetchPrescriptions();
        } catch (err) {
            alert('Eroare: ' + (err.response?.data?.message || 'Verifică datele'));
        }
    };

    const handleOpenCancel = (p) => {
        setSelectedId(p.id);
        setCancelReason('');
        setOpenCancel(true);
    };

    const handleConfirmCancel = async () => {
        if (!cancelReason.trim()) { alert('Motivul anulării este obligatoriu!'); return; }
        try {
            await api.post(`/prescriptions/${selectedId}/cancel`, { reason: cancelReason });
            setOpenCancel(false);
            fetchPrescriptions();
        } catch (err) {
            alert('Eroare: ' + (err.response?.data?.message || 'Eroare server'));
        }
    };

    const handleConfirmDelete = async () => {
        try {
            await api.delete(`/prescriptions/${selectedId}`);
            setOpenDelete(false);
            fetchPrescriptions();
        } catch (err) {
            alert('Eroare la ștergere: ' + (err.response?.data?.message || 'Eroare server'));
        }
    };

    const updateItem = (idx, field, value) => {
        const items = [...newPrescription.items];
        items[idx] = { ...items[idx], [field]: value };
        setNewPrescription({ ...newPrescription, items });
    };
    const addItem = () =>
        setNewPrescription({ ...newPrescription, items: [...newPrescription.items, { ...EMPTY_ITEM }] });
    const removeItem = (idx) =>
        setNewPrescription({ ...newPrescription, items: newPrescription.items.filter((_, i) => i !== idx) });

    // ---- Filtrare ----
    const filtered = prescriptions.filter(p => {
        const term = searchTerm.toLowerCase();
        const matchesSearch =
            (p.uniqueCode || '').toLowerCase().includes(term) ||
            (p.doctor ? `${p.doctor.firstName} ${p.doctor.lastName}` : '').toLowerCase().includes(term) ||
            (p.patient ? `${p.patient.firstName} ${p.patient.lastName}` : '').toLowerCase().includes(term);

        const matchesPatient = !patientFilter || p.patient?.id === patientFilter;

        return matchesSearch && matchesPatient;
    });

    const paginated = filtered.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Paper sx={{ p: 3, mb: 3, borderRadius: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Box>
                        <Typography variant="h4" fontWeight="bold" color="primary">
                            {isAdmin ? 'Toate Rețetele' : isDoctor ? 'Rețetele Prescrise' : 'Rețetele Mele'}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                            {filtered.length} rețete afișate din {prescriptions.length} total.
                        </Typography>
                    </Box>
                    {isDoctor && (
                        <Button variant="contained" startIcon={<Add />} onClick={() => setOpenAdd(true)}>
                            Rețetă nouă
                        </Button>
                    )}
                </Box>

                {/* Filtre */}
                <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                    <TextField
                        label="Caută după cod, doctor sau pacient..."
                        variant="outlined"
                        sx={{ flexGrow: 1 }}
                        value={searchTerm}
                        onChange={(e) => { setSearchTerm(e.target.value); setPage(0); }}
                    />
                    {/* Filtru per pacient — doar pentru DOCTOR */}
                    {isDoctor && uniquePatientsInPrescriptions.length > 0 && (
                        <TextField
                            select
                            label="Filtrează după pacient"
                            sx={{ minWidth: 220 }}
                            value={patientFilter}
                            onChange={(e) => { setPatientFilter(e.target.value); setPage(0); }}
                        >
                            <MenuItem value="">Toți pacienții</MenuItem>
                            {uniquePatientsInPrescriptions.map(p => (
                                <MenuItem key={p.id} value={p.id}>
                                    {p.firstName} {p.lastName}
                                </MenuItem>
                            ))}
                        </TextField>
                    )}
                </Box>
            </Paper>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 3 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                        <TableRow>
                            <TableCell><strong>Cod Unic</strong></TableCell>
                            {(isPatient || isAdmin) && <TableCell><strong>Doctor</strong></TableCell>}
                            {(isDoctor || isAdmin) && <TableCell><strong>Pacient</strong></TableCell>}
                            <TableCell><strong>Status</strong></TableCell>
                            <TableCell><strong>Data Emiterii</strong></TableCell>
                            <TableCell align="right"><strong>Detalii / Acțiuni</strong></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginated.length > 0 ? paginated.map((row) => (
                            <PrescriptionRow
                                key={row.id}
                                row={row}
                                role={role}
                                onEdit={handleOpenEdit}
                                onCancel={handleOpenCancel}
                                onDelete={(id) => { setSelectedId(id); setOpenDelete(true); }}
                            />
                        )) : (
                            <TableRow>
                                <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                                    Nu au fost găsite rețete.
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

            {/* ---- Dialog Adaugare (DOCTOR) ---- */}
            <Dialog open={openAdd} onClose={() => setOpenAdd(false)} fullWidth maxWidth="sm">
                <DialogTitle fontWeight="bold">Emite Rețetă Nouă</DialogTitle>
                <DialogContent>
                    <TextField
                        select fullWidth margin="normal" label="Pacient"
                        value={newPrescription.patientId}
                        onChange={(e) => setNewPrescription({ ...newPrescription, patientId: e.target.value })}
                    >
                        <MenuItem value="">-- Selectează pacientul --</MenuItem>
                        {patients.map(p => (
                            <MenuItem key={p.id} value={p.id}>{p.firstName} {p.lastName}</MenuItem>
                        ))}
                    </TextField>
                    <TextField
                        label="Note Doctor (opțional)" fullWidth margin="normal" multiline rows={2}
                        value={newPrescription.doctorNotes}
                        onChange={(e) => setNewPrescription({ ...newPrescription, doctorNotes: e.target.value })}
                    />
                    <Typography variant="subtitle1" fontWeight="bold" sx={{ mt: 2, mb: 1 }}>
                        Medicamente prescrise
                    </Typography>
                    {newPrescription.items.map((item, idx) => (
                        <Paper key={idx} variant="outlined" sx={{ p: 2, mb: 2, borderRadius: 2 }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                <Typography variant="body2" fontWeight="bold">Medicament #{idx + 1}</Typography>
                                {newPrescription.items.length > 1 && (
                                    <Button size="small" color="error" onClick={() => removeItem(idx)}>Elimină</Button>
                                )}
                            </Box>
                            <TextField
                                select fullWidth margin="dense" label="Substanță Activă"
                                value={item.activeSubstanceId}
                                onChange={(e) => updateItem(idx, 'activeSubstanceId', e.target.value)}
                            >
                                <MenuItem value="">-- Selectează substanța --</MenuItem>
                                {substances.map(s => (
                                    <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>
                                ))}
                            </TextField>
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                <TextField label="Doză" margin="dense" fullWidth value={item.dose}
                                    onChange={(e) => updateItem(idx, 'dose', e.target.value)} placeholder="ex: 500mg" />
                                <TextField label="Frecvență" margin="dense" fullWidth value={item.frequency}
                                    onChange={(e) => updateItem(idx, 'frequency', e.target.value)} placeholder="ex: 2x/zi" />
                                <TextField label="Zile" margin="dense" type="number" sx={{ width: 90 }} value={item.durationDays}
                                    onChange={(e) => updateItem(idx, 'durationDays', e.target.value)} />
                            </Box>
                        </Paper>
                    ))}
                    <Button variant="outlined" onClick={addItem} startIcon={<Add />} fullWidth>
                        Adaugă medicament
                    </Button>
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button onClick={() => setOpenAdd(false)}>Anulează</Button>
                    <Button onClick={handleAddPrescription} variant="contained">Emite Rețeta</Button>
                </DialogActions>
            </Dialog>

            {/* ---- Dialog Editare (ADMIN only) ---- */}
            <Dialog open={openEdit} onClose={() => setOpenEdit(false)} fullWidth maxWidth="xs">
                <DialogTitle fontWeight="bold">Editează Rețetă</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Note Doctor" fullWidth margin="normal" multiline rows={3}
                        value={editData.doctorNotes}
                        onChange={(e) => setEditData({ ...editData, doctorNotes: e.target.value })}
                    />
                    <TextField
                        select fullWidth margin="normal" label="Status"
                        value={editData.status}
                        onChange={(e) => setEditData({ ...editData, status: e.target.value })}
                    >
                        {['PRESCRIBED', 'FULFILLED', 'PARTIALLY_FULFILLED', 'CANCELLED'].map(s => (
                            <MenuItem key={s} value={s}>{s}</MenuItem>
                        ))}
                    </TextField>
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button onClick={() => setOpenEdit(false)}>Anulează</Button>
                    <Button onClick={handleSaveEdit} variant="contained">Salvează</Button>
                </DialogActions>
            </Dialog>

            {/* ---- Dialog Anulare ---- */}
            <Dialog open={openCancel} onClose={() => setOpenCancel(false)} fullWidth maxWidth="xs">
                <DialogTitle fontWeight="bold" sx={{ color: 'warning.dark' }}>Anulează Rețeta</DialogTitle>
                <DialogContent>
                    <DialogContentText sx={{ mb: 2 }}>
                        Introdu motivul pentru care anulezi această rețetă. Motivul va fi salvat în sistem.
                    </DialogContentText>
                    <TextField
                        label="Motiv anulare *"
                        fullWidth
                        multiline
                        rows={3}
                        value={cancelReason}
                        onChange={(e) => setCancelReason(e.target.value)}
                        placeholder="ex: Pacientul a raportat o reacție adversă la substanță..."
                        autoFocus
                    />
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button onClick={() => setOpenCancel(false)}>Renunță</Button>
                    <Button onClick={handleConfirmCancel} color="warning" variant="contained">
                        Anulează Rețeta
                    </Button>
                </DialogActions>
            </Dialog>

            {/* ---- Dialog Stergere ---- */}
            <Dialog open={openDelete} onClose={() => setOpenDelete(false)}>
                <DialogTitle>Confirmi ștergerea?</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Rețeta va fi eliminată permanent din sistem. Acțiunea nu poate fi anulată.
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

export default PrescriptionsPage;