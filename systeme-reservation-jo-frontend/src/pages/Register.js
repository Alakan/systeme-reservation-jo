// src/pages/Register.js
import { useState } from 'react';
import api from '../services/api';
import '../styles/Register.css';

function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleRegister = async (e) => {
    e.preventDefault();
    console.log("Tentative d'inscription :", { username, email, password });

    try {
      // Note : pas de slash initial pour que le chemin soit concaténé avec baseURL
      const response = await api.post('auth/register', JSON.stringify({ username, email, password }), {
        headers: { 'Content-Type': 'application/json' }
      });

      console.log("Réponse backend :", response.data);
      alert("Inscription réussie !");
      window.location.href = "/login";
    } catch (error) {
      console.error("Erreur d'inscription :", error.response?.data || error);
      alert("Échec de l'inscription. Vérifie tes informations.");
    }
  };

  return (
    <div className="register-container">
      <h1>Inscription</h1>
      <form onSubmit={handleRegister}>
        <input
          type="text"
          placeholder="Nom d'utilisateur"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Mot de passe"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">S'inscrire</button>
      </form>
    </div>
  );
}

export default Register;
