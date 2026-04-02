import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import Navbar from './components/Navbar';
import { Box } from '@mui/material';
import PrescriptionsPage from './pages/PrescriptionsPage';
import AllergiesPage from './pages/AllergiesPage';
import DashboardPage from './pages/DashboardPage';
import FeedbackPage from './pages/FeedbackPage';
import AdminUsersPage from './pages/AdminUsersPage';
import AdminApprovalsPage from './pages/AdminApprovalsPage';

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
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/prescriptions" element={<PrescriptionsPage />} />
                    <Route path="/allergies" element={<AllergiesPage />} />

                    <Route path="/dashboard" element={<DashboardPage />} />
                    <Route path="/feedback" element={<FeedbackPage />} />
                    <Route path="/admin/users" element={<AdminUsersPage />} />
                    <Route path="/admin/approvals" element={<AdminApprovalsPage />} />

                    {/* Fallback pentru rute inexistente */}
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </Box>
        </>
    );
}

export default App;