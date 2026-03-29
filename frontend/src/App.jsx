import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import Navbar from './components/Navbar';
import { Box } from '@mui/material';
import PrescriptionsPage from './pages/PrescriptionsPage';
import AllergiesPage from './pages/AllergiesPage'; // Asigură-te că importul e corect

function App() {
    const location = useLocation();
    const isAuthenticated = !!localStorage.getItem('token');

    // Nu afișăm Navbar-ul pe pagina de Login
    const showNavbar = isAuthenticated && location.pathname !== '/login';

    return (
        <>
            {showNavbar && <Navbar />}

            <Box sx={{ p: 3 }}> {/* Padding global pentru conținut */}
                <Routes>
                    <Route path="/" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/prescriptions" element={<PrescriptionsPage />} />
                    <Route path="/allergies" element={<AllergiesPage />} />

                    {/* Rute Protejate (Dashboard, Rețete, Alergii) */}
                    <Route path="/dashboard" element={
                        <div>
                            <h1>Bine ai venit în MedConnect</h1>
                            <p>Folosește meniul de mai sus pentru a naviga.</p>
                        </div>
                    } />

                    <Route path="/prescriptions" element={
                        <div>
                            <h1>Modul Rețete</h1>
                            <p>Aici va apărea tabelul cu rețete (4 puncte în barem).</p>
                        </div>
                    } />

                    <Route path="/allergies" element={
                        <div>
                            <h1>Modul Alergii / Stocuri</h1>
                            <p>Aici vom gestiona alergiile sau stocurile farmaciei.</p>
                        </div>
                    } />

                    {/* Fallback pentru rute inexistente */}
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </Box>
        </>
    );
}

export default App;