import axios from 'axios';

// Construit l'URL de base à partir de la variable d'environnement REACT_APP_API_URL,
// ou utilise 'http://localhost:8080' si elle n'est pas définie.
const baseURL = process.env.REACT_APP_API_URL ? `${process.env.REACT_APP_API_URL}/api` : 'http://localhost:8080/api';

const api = axios.create({
    baseURL: baseURL,
    headers: {
        'Content-Type': 'application/json',
    }
});

export default api;
