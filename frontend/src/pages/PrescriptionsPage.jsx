import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Paper, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, TextField, TablePagination,
    CircularProgress, Box, Chip
} from '@mui/material';
import api from '../api/axios';

const PrescriptionsPage = () => {
    const [prescriptions, setPrescriptions] = useState([]);
    const [loading, setLoading] = useState(true);

    // State-uri pentru cerințele de barem (Căutare + Paginare)
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const role = localStorage.getItem('role');
            console.log("Rolul detectat în React:", role); // <--- DEBUG 1

            // Verificăm dacă ruta aleasă e corectă
            let endpoint = '/prescriptions/my-prescriptions';
            if (role === 'ADMIN') {
                endpoint = '/prescriptions/all';
            }

            console.log("Apelăm endpoint-ul:", endpoint); // <--- DEBUG 2

            const response = await api.get(endpoint);
            setPrescriptions(response.data);
        } catch (error) {
            console.error("Eroare la încărcare:", error);
        } finally {
            setLoading(false);
        }
    };

    // --- LOGICA DE FILTRARE (3 puncte barem) ---
    const filteredPrescriptions = prescriptions.filter(p => {
        const code = p.uniqueCode?.toLowerCase() || '';
        const doctorFirst = p.doctor?.firstName?.toLowerCase() || '';
        const doctorLast = p.doctor?.lastName?.toLowerCase() || '';
        const search = searchTerm.toLowerCase();

        return code.includes(search) || doctorFirst.includes(search) || doctorLast.includes(search);
    });

    // --- LOGICA DE PAGINARE (2 puncte barem) ---
    const handleChangePage = (event, newPage) => setPage(newPage);
    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    // Funcție pentru culori status
    const getStatusColor = (status) => {
        switch(status) {
            case 'FULFILLED': return 'success';
            case 'PRESCRIBED': return 'primary';
            case 'CANCELLED': return 'error';
            default: return 'warning';
        }
    };

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
                <Typography variant="h4" fontWeight="bold" gutterBottom color="primary">
                    {localStorage.getItem('role') === 'ADMIN' ? 'Sistem Monitorizare Rețete' : 'Rețetele Mele'}
                </Typography>
                <Typography variant="body1" color="textSecondary" sx={{ mb: 2 }}>
                    {prescriptions.length} rețete găsite în sistem.
                </Typography>

                {/* SEARCH FIELD (Cerință Barem) */}
                <TextField
                    label="Caută după cod rețetă sau nume doctor..."
                    variant="outlined"
                    fullWidth
                    value={searchTerm}
                    onChange={(e) => {
                        setSearchTerm(e.target.value);
                        setPage(0); // Resetăm pagina la căutare
                    }}
                    placeholder="Ex: MED-8A2C..."
                />
            </Paper>

            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 3 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                        <TableRow>
                            <TableCell><strong>Cod Unic</strong></TableCell>
                            <TableCell><strong>Doctor</strong></TableCell>
                            <TableCell><strong>Status</strong></TableCell>
                            <TableCell><strong>Data Emiterii</strong></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredPrescriptions.length > 0 ? (
                            filteredPrescriptions
                                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((row) => (
                                    <TableRow key={row.id} hover>
                                        <TableCell sx={{ fontWeight: 'medium', color: '#1a237e' }}>
                                            {row.uniqueCode}
                                        </TableCell>
                                        <TableCell>
                                            {row.doctor ? `Dr. ${row.doctor.firstName} ${row.doctor.lastName}` : 'Nespecificat'}
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={row.status}
                                                color={getStatusColor(row.status)}
                                                size="small"
                                                variant="outlined"
                                                sx={{ fontWeight: 'bold' }}
                                            />
                                        </TableCell>
                                        <TableCell>
                                            {row.prescribedAt ? new Date(row.prescribedAt).toLocaleDateString('ro-RO') : 'N/A'}
                                        </TableCell>
                                    </TableRow>
                                ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={4} align="center" sx={{ py: 3 }}>
                                    Nu au fost găsite rețete.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>

                {/* PAGINATION (Cerință Barem) */}
                <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={filteredPrescriptions.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                    labelRowsPerPage="Rânduri pe pagină:"
                />
            </TableContainer>
        </Container>
    );
};

export default PrescriptionsPage;