// src/pages/Login.js
import { Link } from 'react-router-dom';
import { useState } from 'react';
import api from '../services/api';
import '../styles/Login.css';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    console.log("Tentative de connexion :", { email, password });

    try {
      // Remarque : "auth/login" est utilisé sans slash initial pour concaténer avec la baseURL
      const response = await api.post(
        'auth/login',
        JSON.stringify({ email, password }),
        { headers: { 'Content-Type': 'application/json' } }
      );

      const token = response.data.token;
      localStorage.setItem("token", token);
      console.log("Token sauvegardé :", localStorage.getItem("token"));

      alert("Connexion réussie !");
      window.location.href = "/dashboard"; // Redirection vers le dashboard de l'utilisateur
    } catch (error) {
      console.error("Erreur de connexion :", error.response?.data || error);
      alert("Échec de la connexion. Vérifie tes informations.");
    }
  };

  return (
    <div className="login-container">
      <h1>Connexion</h1>
      <form onSubmit={handleLogin}>
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
        <button type="submit">Se connecter</button>
      </form>
      <div className="signup-link">
        <p>Vous n'avez pas de compte ? <Link to="/register">Créez-en un ici</Link></p>
      </div>
      <Link to="/"><button className="btn-home">Retour à l’accueil</button></Link>
    </div>
  );
}

export default Login;
