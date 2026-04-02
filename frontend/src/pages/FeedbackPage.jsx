import React, { useState, useEffect } from 'react';
import {
    Container, Paper, Typography, Box, TextField, Button, Alert,
    FormControl, FormLabel, RadioGroup, FormControlLabel, Radio,
    Select, InputLabel, MenuItem, Checkbox, FormGroup,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Chip, CircularProgress, Tooltip
} from '@mui/material';
import { Feedback as FeedbackIcon, ContactMail } from '@mui/icons-material';
import api from '../api/axios';

const CATEGORIES = [
    { value: 'APP_ISSUE', label: 'Problemă cu aplicația' },
    { value: 'PHARMACY_ISSUE', label: 'Problemă cu o farmacie' },
    { value: 'DOCTOR_ISSUE', label: 'Problemă cu un doctor' },
    { value: 'GENERAL_SUGGESTION', label: 'Sugestie generală' },
];

const CATEGORY_LABEL = {
    APP_ISSUE: 'Problemă aplicație',
    PHARMACY_ISSUE: 'Problemă farmacie',
    DOCTOR_ISSUE: 'Problemă doctor',
    GENERAL_SUGGESTION: 'Sugestie',
};

const CATEGORY_COLOR = {
    APP_ISSUE: 'error',
    PHARMACY_ISSUE: 'warning',
    DOCTOR_ISSUE: 'info',
    GENERAL_SUGGESTION: 'success',
};

const RATINGS = [
    { value: 'ONE_STAR', label: '⭐ 1 — Foarte slab' },
    { value: 'TWO_STARS', label: '⭐⭐ 2 — Slab' },
    { value: 'THREE_STARS', label: '⭐⭐⭐ 3 — Acceptabil' },
    { value: 'FOUR_STARS', label: '⭐⭐⭐⭐ 4 — Bun' },
    { value: 'FIVE_STARS', label: '⭐⭐⭐⭐⭐ 5 — Excelent' },
];

const RATING_STARS = {
    ONE_STAR: '⭐',
    TWO_STARS: '⭐⭐',
    THREE_STARS: '⭐⭐⭐',
    FOUR_STARS: '⭐⭐⭐⭐',
    FIVE_STARS: '⭐⭐⭐⭐⭐',
};

// ---- Vizualizare admin ----
const AdminFeedbackView = () => {
    const [feedbacks, setFeedbacks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        api.get('/feedback')
            .then(res => setFeedbacks(res.data))
            .catch(() => setError('Eroare la încărcarea feedback-urilor.'))
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}><CircularProgress /></Box>;
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 6 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                <FeedbackIcon color="primary" sx={{ fontSize: 40 }} />
                <Box>
                    <Typography variant="h4" fontWeight="bold" color="primary">
                        Feedback utilizatori
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {feedbacks.length} mesaje primite
                    </Typography>
                </Box>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <Paper sx={{ borderRadius: 2 }}>
                <TableContainer>
                    <Table>
                        <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                            <TableRow>
                                <TableCell><strong>Utilizator</strong></TableCell>
                                <TableCell><strong>Categorie</strong></TableCell>
                                <TableCell><strong>Rating</strong></TableCell>
                                <TableCell><strong>Detalii</strong></TableCell>
                                <TableCell><strong>Contact</strong></TableCell>
                                <TableCell><strong>Data</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {feedbacks.length > 0 ? feedbacks.map(row => (
                                <TableRow key={row.id} hover>
                                    <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                                        {row.userEmail}
                                    </TableCell>
                                    <TableCell>
                                        <Chip
                                            label={CATEGORY_LABEL[row.category] || row.category}
                                            color={CATEGORY_COLOR[row.category] || 'default'}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>{RATING_STARS[row.rating] || row.rating}</TableCell>
                                    <TableCell sx={{ maxWidth: 320, whiteSpace: 'pre-wrap' }}>
                                        {row.details}
                                    </TableCell>
                                    <TableCell>
                                        {row.allowContact ? (
                                            <Tooltip title="Utilizatorul acceptă să fie contactat">
                                                <ContactMail color="success" fontSize="small" />
                                            </Tooltip>
                                        ) : '—'}
                                    </TableCell>
                                    <TableCell sx={{ whiteSpace: 'nowrap', fontSize: '0.85rem' }}>
                                        {row.createdAt ? new Date(row.createdAt).toLocaleDateString('ro-RO') : '—'}
                                    </TableCell>
                                </TableRow>
                            )) : (
                                <TableRow>
                                    <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                                        Nu există feedback-uri înregistrate.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Container>
    );
};

// ---- Formular pentru utilizatori obișnuiți ----
const UserFeedbackForm = () => {
    const [form, setForm] = useState({
        category: '',
        rating: '',
        details: '',
        allowContact: false,
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!form.category) { setError('Selectează o categorie!'); return; }
        if (!form.rating) { setError('Selectează un rating!'); return; }
        if (!form.details.trim()) { setError('Completează câmpul de detalii!'); return; }

        setLoading(true);
        try {
            await api.post('/feedback', form);
            setSuccess('Feedback-ul tău a fost trimis cu succes! Mulțumim!');
            setForm({ category: '', rating: '', details: '', allowContact: false });
        } catch (err) {
            setError(err.response?.data?.message || 'Eroare la trimitere. Încearcă din nou.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container maxWidth="sm" sx={{ mt: 4, mb: 4 }}>
            <Paper elevation={4} sx={{ p: 4, borderRadius: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                    <FeedbackIcon color="primary" sx={{ fontSize: 40 }} />
                    <Box>
                        <Typography variant="h4" fontWeight="bold" color="primary">
                            Feedback
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Ajută-ne să îmbunătățim MedConnect
                        </Typography>
                    </Box>
                </Box>

                {error && <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ mb: 2, borderRadius: 2 }}>{success}</Alert>}

                <form onSubmit={handleSubmit}>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Categorie *</InputLabel>
                        <Select
                            value={form.category}
                            label="Categorie *"
                            onChange={(e) => setForm({ ...form, category: e.target.value })}
                        >
                            {CATEGORIES.map(c => (
                                <MenuItem key={c.value} value={c.value}>{c.label}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl component="fieldset" margin="normal" fullWidth>
                        <FormLabel component="legend" sx={{ fontWeight: 'bold', mb: 1 }}>
                            Evaluare *
                        </FormLabel>
                        <RadioGroup
                            value={form.rating}
                            onChange={(e) => setForm({ ...form, rating: e.target.value })}
                        >
                            {RATINGS.map(r => (
                                <FormControlLabel
                                    key={r.value}
                                    value={r.value}
                                    control={<Radio />}
                                    label={r.label}
                                />
                            ))}
                        </RadioGroup>
                    </FormControl>

                    <TextField
                        label="Detalii *"
                        fullWidth
                        margin="normal"
                        multiline
                        rows={4}
                        value={form.details}
                        onChange={(e) => setForm({ ...form, details: e.target.value })}
                        placeholder="Descrie problema sau sugestia ta în detaliu..."
                    />

                    <FormGroup sx={{ mt: 1 }}>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    checked={form.allowContact}
                                    onChange={(e) => setForm({ ...form, allowContact: e.target.checked })}
                                />
                            }
                            label="Permit echipei MedConnect să mă contacteze pentru mai multe detalii"
                        />
                    </FormGroup>

                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        size="large"
                        disabled={loading || !!success}
                        sx={{ mt: 3, py: 1.5, borderRadius: 2, fontWeight: 'bold', textTransform: 'none', fontSize: '1rem' }}
                    >
                        {loading ? 'Se trimite...' : 'Trimite Feedback'}
                    </Button>
                </form>
            </Paper>
        </Container>
    );
};

const FeedbackPage = () => {
    const role = localStorage.getItem('role');
    return role === 'ADMIN' ? <AdminFeedbackView /> : <UserFeedbackForm />;
};

export default FeedbackPage;