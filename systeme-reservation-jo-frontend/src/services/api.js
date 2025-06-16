import axios from 'axios';

// La variable d'environnement contient déjà le segment "/api"
const baseURL = process.env.REACT_APP_API_URL
  ? process.env.REACT_APP_API_URL
  : 'http://localhost:8080/api';

const api = axios.create({
  baseURL,
  headers: {
    // On précise charset et types acceptés
    'Content-Type': 'application/json; charset=UTF-8',
    Accept: 'application/json; charset=UTF-8'
  }
});

export default api;
