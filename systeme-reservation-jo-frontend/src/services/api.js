// src/services/api.js
import axios from 'axios';

// La variable d'environnement contient déjà le segment "/api"
const baseURL = process.env.REACT_APP_API_URL
  ? `${process.env.REACT_APP_API_URL}`
  : 'http://localhost:8080/api';

const api = axios.create({
  baseURL,
- headers: {
-   'Content-Type': 'application/json',
- },
+ headers: {
+   // On précise explicitement le charset pour que le navigateur
+   // décode bien la réponse en UTF-8 et encode les requêtes en UTF-8
+   'Content-Type': 'application/json; charset=UTF-8',
+   'Accept':       'application/json; charset=UTF-8'
+ },
});

export default api;
