import React, { useState } from 'react';
import { Container, Paper, TextField, Button, Typography, Box, Alert, InputAdornment, IconButton } from '@mui/material';
import { Visibility, VisibilityOff, LockOutlined } from '@mui/icons-material';
import api from '../api/axios';
import { useNavigate, Link } from 'react-router-dom';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await api.post('/v1/auth/login', {
                email: email,
                password: password
            });

            // 1. Salvăm Token-ul
            localStorage.setItem('token', response.data.token);

            // 2. Salvăm Rolul (Asigură-te că backend-ul tău returnează response.data.role)
            // Dacă backend-ul nu returnează rolul separat, va trebui să decodăm JWT-ul mai târziu.
            if (response.data.role) {
                localStorage.setItem('role', response.data.role);
            }

            // 3. Redirecționăm
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.message || 'Email sau parolă incorectă!');
            console.error(err);
        }
    };

    return (
        <Container maxWidth="xs">
            <Box sx={{ marginTop: 10, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Paper elevation={6} sx={{ padding: 4, width: '100%', borderRadius: 3 }}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
                        <IconButton sx={{ bgcolor: 'primary.main', color: 'white', mb: 1, "&:hover": { bgcolor: 'primary.dark' } }}>
                            <LockOutlined />
                        </IconButton>
                        <Typography variant="h4" component="h1" fontWeight="bold" color="primary">
                            MedConnect
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                            Introdu datele pentru a accesa contul
                        </Typography>
                    </Box>

                    {error && <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>{error}</Alert>}

                    <form onSubmit={handleLogin}>
                        <TextField
                            label="Adresă Email"
                            fullWidth
                            margin="normal"
                            variant="outlined"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            autoComplete="email"
                        />
                        <TextField
                            label="Parolă"
                            type={showPassword ? 'text' : 'password'}
                            fullWidth
                            margin="normal"
                            variant="outlined"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
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
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            size="large"
                            sx={{
                                mt: 3,
                                mb: 2,
                                py: 1.5,
                                borderRadius: 2,
                                fontWeight: 'bold',
                                textTransform: 'none',
                                fontSize: '1rem'
                            }}
                        >
                            Autentificare
                        </Button>
                    </form>

                    <Box sx={{ mt: 2, textAlign: 'center' }}>
                        <Typography variant="body2">
                            Nu ai cont?{' '}
                            <Link to="/register" style={{ color: '#1976d2', textDecoration: 'none', fontWeight: 'bold' }}>
                                Înregistrează-te
                            </Link>
                        </Typography>
                    </Box>
                </Paper>
            </Box>
        </Container>
    );
};

export default LoginPage;