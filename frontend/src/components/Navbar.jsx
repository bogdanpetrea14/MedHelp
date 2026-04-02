import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
    const navigate = useNavigate();
    const role = localStorage.getItem('role');
    const isAdmin = role === 'ADMIN';

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        navigate('/login');
    };

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" sx={{ flexGrow: 1 }}>
                    MedConnect
                </Typography>
                <Box>
                    <Button color="inherit" onClick={() => navigate('/dashboard')}>Dashboard</Button>
                    <Button color="inherit" onClick={() => navigate('/prescriptions')}>Rețete</Button>
                    <Button color="inherit" onClick={() => navigate('/allergies')}>Alergii</Button>
                    <Button color="inherit" onClick={() => navigate('/feedback')}>Feedback</Button>
                    {isAdmin && (
                        <Button color="inherit" onClick={() => navigate('/admin/users')}>
                            Utilizatori
                        </Button>
                    )}
                    {isAdmin && (
                        <Button color="inherit" onClick={() => navigate('/admin/approvals')}>
                            Aprobări
                        </Button>
                    )}
                    <Button color="inherit" sx={{ ml: 2, bgcolor: 'error.main' }} onClick={handleLogout}>Logout</Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;