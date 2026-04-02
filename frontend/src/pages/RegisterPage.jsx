import React, { useState } from 'react';
import {
    Container, Paper, TextField, Button, Typography, Box, Alert,
    InputAdornment, IconButton, MenuItem
} from '@mui/material';
import { Visibility, VisibilityOff, PersonAddAlt1 } from '@mui/icons-material';
import api from '../api/axios';
import { useNavigate, Link } from 'react-router-dom';

const ROLES = [
    { value: 'PATIENT', label: 'Pacient' },
    { value: 'DOCTOR', label: 'Doctor' },
    { value: 'PHARMACY', label: 'Farmacie' },
];

const RegisterPage = () => {
    const [form, setForm] = useState({ email: '', password: '', confirmPassword: '', role: 'PATIENT' });
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        setError('');
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (form.password !== form.confirmPassword) {
            setError('Parolele nu coincid!');
            return;
        }
        if (form.password.length < 6) {
            setError('Parola trebuie să aibă cel puțin 6 caractere.');
            return;
        }

        try {
            await api.post('/v1/auth/register', {
                email: form.email,
                password: form.password,
                role: form.role,
            });
            const needsApproval = form.role === 'DOCTOR' || form.role === 'PHARMACY';
            if (needsApproval) {
                setSuccess('Cont creat! Contul tău necesită aprobarea unui administrator înainte de a te putea autentifica.');
            } else {
                setSuccess('Cont creat cu succes! Vei fi redirecționat spre login...');
                setTimeout(() => navigate('/login'), 2000);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Eroare la înregistrare. Încearcă din nou.');
        }
    };

    return (
        <Container maxWidth="xs">
            <Box sx={{ marginTop: 10, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Paper elevation={6} sx={{ padding: 4, width: '100%', borderRadius: 3 }}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
                        <IconButton sx={{ bgcolor: 'primary.main', color: 'white', mb: 1, '&:hover': { bgcolor: 'primary.dark' } }}>
                            <PersonAddAlt1 />
                        </IconButton>
                        <Typography variant="h4" component="h1" fontWeight="bold" color="primary">
                            Cont nou
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                            Creează-ți contul MedConnect
                        </Typography>
                    </Box>

                    {error && <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>{error}</Alert>}
                    {success && <Alert severity="success" sx={{ mb: 2, borderRadius: 2 }}>{success}</Alert>}

                    <form onSubmit={handleRegister}>
                        <TextField
                            label="Adresă Email"
                            name="email"
                            type="email"
                            fullWidth
                            margin="normal"
                            variant="outlined"
                            value={form.email}
                            onChange={handleChange}
                            required
                            autoComplete="email"
                        />
                        <TextField
                            select
                            label="Rol"
                            name="role"
                            fullWidth
                            margin="normal"
                            variant="outlined"
                            value={form.role}
                            onChange={handleChange}
                        >
                            {ROLES.map((r) => (
                                <MenuItem key={r.value} value={r.value}>{r.label}</MenuItem>
                            ))}
                        </TextField>
                        {(form.role === 'DOCTOR' || form.role === 'PHARMACY') && (
                            <Alert severity="info" sx={{ mt: 1, borderRadius: 2 }}>
                                Conturile de <strong>{form.role === 'DOCTOR' ? 'Doctor' : 'Farmacie'}</strong> necesită
                                aprobarea unui administrator înainte de a putea fi folosite.
                            </Alert>
                        )}
                        <TextField
                            label="Parolă"
                            name="password"
                            type={showPassword ? 'text' : 'password'}
                            fullWidth
                            margin="normal"
                            variant="outlined"
                            value={form.password}
                            onChange={handleChange}
                            required
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                                            {showPassword ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }}
                        />
                        <TextField
                            label="Confirmă Parola"
                            name="confirmPassword"
                            type={showConfirm ? 'text' : 'password'}
                            fullWidth
                            margin="normal"
                            variant="outlined"
                            value={form.confirmPassword}
                            onChange={handleChange}
                            required
                            error={form.confirmPassword.length > 0 && form.password !== form.confirmPassword}
                            helperText={
                                form.confirmPassword.length > 0 && form.password !== form.confirmPassword
                                    ? 'Parolele nu coincid'
                                    : ''
                            }
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton onClick={() => setShowConfirm(!showConfirm)} edge="end">
                                            {showConfirm ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }}
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            size="large"
                            disabled={!!success}
                            sx={{
                                mt: 3,
                                mb: 2,
                                py: 1.5,
                                borderRadius: 2,
                                fontWeight: 'bold',
                                textTransform: 'none',
                                fontSize: '1rem',
                            }}
                        >
                            Creează cont
                        </Button>
                    </form>

                    <Box sx={{ mt: 1, textAlign: 'center' }}>
                        <Typography variant="body2">
                            Ai deja cont?{' '}
                            <Link to="/login" style={{ color: '#1976d2', textDecoration: 'none', fontWeight: 'bold' }}>
                                Autentifică-te
                            </Link>
                        </Typography>
                    </Box>
                </Paper>
            </Box>
        </Container>
    );
};

export default RegisterPage;