import React, { useState } from 'react';
import { Container, Paper, TextField, Button, Typography, Box, Alert } from '@mui/material';
import api from '../api/axios'; // Importăm instanța noastră de axios
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            // Trimitem cererea la endpoint-ul tău de auth din Spring
            // Verifică dacă în backend ai /api/v1/auth/login sau doar /api/auth/login
            const response = await api.post('/v1/auth/login', {
                email: email,
                password: password
            });

            // Salvăm token-ul în localStorage pentru a-l folosi la viitoarele cereri
            localStorage.setItem('token', response.data.token);

            // Redirecționăm către dashboard (îl facem imediat)
            navigate('/dashboard');
        } catch (err) {
            setError('Email sau parolă incorectă! Verifică dacă serverul este pornit.');
            console.error(err);
        }
    };

    return (
        <Container maxWidth="xs">
            <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
                    <Typography variant="h5" align="center" gutterBottom>
                        MedConnect - Autentificare
                    </Typography>

                    {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                    <form onSubmit={handleLogin}>
                        <TextField
                            label="Email"
                            fullWidth
                            margin="normal"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        <TextField
                            label="Parolă"
                            type="password"
                            fullWidth
                            margin="normal"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2, bgcolor: '#1976d2' }}
                        >
                            Log In
                        </Button>
                    </form>
                </Paper>
            </Box>
        </Container>
    );
};

export default LoginPage;