import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Box, Grid, Card, CardContent, CardActions,
    Chip, Collapse, Divider, CircularProgress, Alert,
    IconButton, Tooltip, Paper
} from '@mui/material';
import {
    MedicalServices, CheckCircle, Cancel, HourglassEmpty,
    ExpandMore, ExpandLess, LocalHospital, CalendarToday,
    Assignment, Science
} from '@mui/icons-material';
import api from '../api/axios';

const statusConfig = {
    PRESCRIBED: {
        label: 'Prescrisă',
        color: 'primary',
        icon: <HourglassEmpty fontSize="small" />,
        bgColor: '#e3f2fd',
        borderColor: '#1565c0',
    },
    FULFILLED: {
        label: 'Eliberată',
        color: 'success',
        icon: <CheckCircle fontSize="small" />,
        bgColor: '#e8f5e9',
        borderColor: '#2e7d32',
    },
    PARTIALLY_FULFILLED: {
        label: 'Parțial eliberată',
        color: 'warning',
        icon: <HourglassEmpty fontSize="small" />,
        bgColor: '#fff8e1',
        borderColor: '#f57f17',
    },
    CANCELLED: {
        label: 'Anulată',
        color: 'error',
        icon: <Cancel fontSize="small" />,
        bgColor: '#fce4ec',
        borderColor: '#b71c1c',
    },
};

const StatCard = ({ title, value, icon, color }) => (
    <Card sx={{ borderRadius: 3, boxShadow: 2, height: '100%' }}>
        <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                        {title}
                    </Typography>
                    <Typography variant="h3" fontWeight="bold" color={color}>
                        {value}
                    </Typography>
                </Box>
                <Box sx={{
                    bgcolor: `${color}.light`,
                    borderRadius: '50%',
                    p: 1.5,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}>
                    {React.cloneElement(icon, { sx: { color: `${color}.dark`, fontSize: 32 } })}
                </Box>
            </Box>
        </CardContent>
    </Card>
);

const PrescriptionCard = ({ prescription }) => {
    const [expanded, setExpanded] = useState(false);
    const config = statusConfig[prescription.status] || statusConfig.PRESCRIBED;
    const isPrescribed = prescription.status === 'PRESCRIBED';

    return (
        <Card sx={{
            borderRadius: 3,
            boxShadow: isPrescribed ? 4 : 2,
            border: `2px solid ${config.borderColor}`,
            bgcolor: config.bgColor,
            transition: 'box-shadow 0.2s',
            '&:hover': { boxShadow: 6 },
        }}>
            <CardContent sx={{ pb: 0 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 1 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Assignment sx={{ color: config.borderColor }} />
                        <Typography variant="h6" fontWeight="bold" sx={{ color: config.borderColor, letterSpacing: 1 }}>
                            {prescription.uniqueCode}
                        </Typography>
                        {isPrescribed && (
                            <Chip label="Necesită ridicare" color="primary" size="small" sx={{ fontWeight: 'bold', animation: 'pulse 2s infinite' }} />
                        )}
                    </Box>
                    <Chip
                        icon={config.icon}
                        label={config.label}
                        color={config.color}
                        variant="filled"
                        sx={{ fontWeight: 'bold' }}
                    />
                </Box>

                <Box sx={{ display: 'flex', gap: 3, mt: 1.5, flexWrap: 'wrap' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                        <LocalHospital fontSize="small" sx={{ color: 'text.secondary' }} />
                        <Typography variant="body2" color="text.secondary">
                            {prescription.doctor
                                ? `Dr. ${prescription.doctor.firstName} ${prescription.doctor.lastName}`
                                : 'Nespecificat'}
                        </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                        <CalendarToday fontSize="small" sx={{ color: 'text.secondary' }} />
                        <Typography variant="body2" color="text.secondary">
                            {prescription.prescribedAt
                                ? new Date(prescription.prescribedAt).toLocaleDateString('ro-RO', {
                                    day: '2-digit', month: 'long', year: 'numeric'
                                })
                                : 'N/A'}
                        </Typography>
                    </Box>
                    {prescription.items?.length > 0 && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                            <Science fontSize="small" sx={{ color: 'text.secondary' }} />
                            <Typography variant="body2" color="text.secondary">
                                {prescription.items.length} medicament{prescription.items.length !== 1 ? 'e' : ''}
                            </Typography>
                        </Box>
                    )}
                </Box>

                {prescription.doctorNotes && (
                    <Box sx={{ mt: 1.5, p: 1.5, bgcolor: 'rgba(255,255,255,0.6)', borderRadius: 2 }}>
                        <Typography variant="body2" fontStyle="italic" color="text.secondary">
                            "{prescription.doctorNotes}"
                        </Typography>
                    </Box>
                )}
            </CardContent>

            {prescription.items?.length > 0 && (
                <>
                    <CardActions sx={{ justifyContent: 'flex-end', pt: 0 }}>
                        <Tooltip title={expanded ? 'Ascunde medicamente' : 'Vezi medicamente'}>
                            <IconButton onClick={() => setExpanded(!expanded)} size="small">
                                {expanded ? <ExpandLess /> : <ExpandMore />}
                                <Typography variant="caption" sx={{ ml: 0.5 }}>
                                    {expanded ? 'Ascunde' : 'Medicamente prescrise'}
                                </Typography>
                            </IconButton>
                        </Tooltip>
                    </CardActions>

                    <Collapse in={expanded} timeout="auto" unmountOnExit>
                        <Divider />
                        <Box sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                            {prescription.items.map((item, idx) => (
                                <Box key={item.id || idx} sx={{
                                    p: 1.5,
                                    bgcolor: 'rgba(255,255,255,0.75)',
                                    borderRadius: 2,
                                    borderLeft: `4px solid ${config.borderColor}`
                                }}>
                                    <Typography variant="subtitle2" fontWeight="bold">
                                        {item.activeSubstance?.name || 'Substanță necunoscută'}
                                    </Typography>
                                    <Box sx={{ display: 'flex', gap: 2, mt: 0.5, flexWrap: 'wrap' }}>
                                        <Typography variant="body2" color="text.secondary">
                                            <strong>Doză:</strong> {item.dose}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            <strong>Frecvență:</strong> {item.frequency}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            <strong>Durată:</strong> {item.durationDays} zile
                                        </Typography>
                                    </Box>
                                    {item.notes && (
                                        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontStyle: 'italic' }}>
                                            {item.notes}
                                        </Typography>
                                    )}
                                </Box>
                            ))}
                        </Box>
                    </Collapse>
                </>
            )}
        </Card>
    );
};

const DashboardPage = () => {
    const [prescriptions, setPrescriptions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const role = localStorage.getItem('role');
    const isPatient = role === 'PATIENT';

    useEffect(() => {
        if (isPatient) {
            fetchPrescriptions();
        } else {
            setLoading(false);
        }
    }, []);

    const fetchPrescriptions = async () => {
        try {
            setLoading(true);
            const response = await api.get('/prescriptions/my-prescriptions');
            setPrescriptions(response.data);
        } catch (err) {
            setError('Nu s-au putut încărca rețetele. Verifică conexiunea cu serverul.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const prescribed = prescriptions.filter(p => p.status === 'PRESCRIBED');
    const fulfilled = prescriptions.filter(p => p.status === 'FULFILLED');
    const other = prescriptions.filter(p => p.status !== 'PRESCRIBED' && p.status !== 'FULFILLED');

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
                <CircularProgress size={48} />
            </Box>
        );
    }

    if (!isPatient) {
        return (
            <Container maxWidth="md" sx={{ mt: 6, textAlign: 'center' }}>
                <MedicalServices sx={{ fontSize: 80, color: 'primary.main', mb: 2 }} />
                <Typography variant="h4" fontWeight="bold" color="primary" gutterBottom>
                    Bine ai venit în MedConnect
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Folosește meniul de navigare pentru a accesa modulele disponibile.
                </Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 6 }}>
            {/* Header */}
            <Paper sx={{ p: 3, mb: 4, borderRadius: 3, background: 'linear-gradient(135deg, #1565c0 0%, #42a5f5 100%)', color: 'white' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <MedicalServices sx={{ fontSize: 48 }} />
                    <Box>
                        <Typography variant="h4" fontWeight="bold">
                            Dosarul meu medical
                        </Typography>
                        <Typography variant="body1" sx={{ opacity: 0.85 }}>
                            Vizualizează și gestionează rețetele tale prescrise
                        </Typography>
                    </Box>
                </Box>
            </Paper>

            {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{error}</Alert>}

            {/* Stats */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={4}>
                    <StatCard
                        title="Total rețete"
                        value={prescriptions.length}
                        icon={<Assignment />}
                        color="primary"
                    />
                </Grid>
                <Grid item xs={12} sm={4}>
                    <StatCard
                        title="De ridicat"
                        value={prescribed.length}
                        icon={<HourglassEmpty />}
                        color={prescribed.length > 0 ? 'warning' : 'success'}
                    />
                </Grid>
                <Grid item xs={12} sm={4}>
                    <StatCard
                        title="Eliberate"
                        value={fulfilled.length}
                        icon={<CheckCircle />}
                        color="success"
                    />
                </Grid>
            </Grid>

            {/* Retete active (PRESCRIBED) */}
            {prescribed.length > 0 && (
                <Box sx={{ mb: 4 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                        <HourglassEmpty color="primary" />
                        <Typography variant="h5" fontWeight="bold" color="primary">
                            Rețete active — de ridicat
                        </Typography>
                        <Chip label={prescribed.length} color="primary" size="small" />
                    </Box>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        {prescribed.map(p => <PrescriptionCard key={p.id} prescription={p} />)}
                    </Box>
                </Box>
            )}

            {/* Retete eliberate / altele */}
            {(fulfilled.length > 0 || other.length > 0) && (
                <Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                        <CheckCircle color="success" />
                        <Typography variant="h5" fontWeight="bold" color="text.primary">
                            Istoric rețete
                        </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        {[...fulfilled, ...other].map(p => <PrescriptionCard key={p.id} prescription={p} />)}
                    </Box>
                </Box>
            )}

            {prescriptions.length === 0 && !error && (
                <Box sx={{ textAlign: 'center', py: 8 }}>
                    <Assignment sx={{ fontSize: 80, color: 'text.disabled', mb: 2 }} />
                    <Typography variant="h6" color="text.secondary">
                        Nu ai nicio rețetă înregistrată momentan.
                    </Typography>
                    <Typography variant="body2" color="text.disabled" sx={{ mt: 1 }}>
                        Rețetele prescrise de medicul tău vor apărea aici.
                    </Typography>
                </Box>
            )}
        </Container>
    );
};

export default DashboardPage;
