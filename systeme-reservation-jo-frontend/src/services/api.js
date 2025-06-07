// src/services/api.js
import axios from 'axios';

// La variable d'environnement est supposée contenir déjà le segment "/api"
// Par exemple, sur Heroku : REACT_APP_API_URL=https://back-systeme-reservation-jo-a366aca13d32.herokuapp.com/api
const baseURL = process.env.REACT_APP_API_URL
  ? `${process.env.REACT_APP_API_URL}`
  : 'http://localhost:8080/api';

const api = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
