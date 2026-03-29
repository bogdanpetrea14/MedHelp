import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8090/api', // Asigură-te că Spring rulează pe 8090
});

// Această funcție va "injecta" Token-ul în fiecare cerere către server
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;