import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Modifie cette URL selon ton environnement
    headers: {
        'Content-Type': 'application/json',
    }
});

export default api;
