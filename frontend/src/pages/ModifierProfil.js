// src/pages/ModifierProfil.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/ModifierProfil.css'; // Créez ce fichier CSS pour styliser la page si besoin

function ModifierProfil() {
  const [profile, setProfile] = useState({
    email: '',
    username: '',
    password: '' // Ce champ est vide par défaut. S'il reste vide, le mot de passe ne sera pas changé.
  });
  const navigate = useNavigate();

  // Récupérer les informations du profil de l'utilisateur connecté
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Vous devez être connecté pour accéder à cette page.');
      navigate('/login');
      return;
    }

    // Appel à l'API pour récupérer le profil
    api.get('/utilisateurs/me', { headers: { Authorization: `Bearer ${token}` } })
      .then(response => {
        // On met à jour le state avec les données récupérées
        const { email, username } = response.data;
        setProfile({
          email: email || '',
          username: username || '',
          password: ''
        });
      })
      .catch(error => {
        console.error("Erreur lors de la récupération du profil :", error);
        alert("Erreur lors de la récupération du profil.");
      });
  }, [navigate]);

  // Fonction pour mettre à jour le state lors d'un changement dans le formulaire
  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  // Fonction de soumission du formulaire
  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');
    if (!token) {
      alert("Vous devez être connecté.");
      return;
    }

    try {
      // Envoi des données de mise à jour vers l'API
      // Si le champ mot de passe est vide, l'API devrait ne pas modifier le mot de passe
      await api.put('/utilisateurs/me', profile, { headers: { Authorization: `Bearer ${token}` } });
      alert("Profil mis à jour avec succès !");
      navigate("/dashboard"); // Redirige vers le dashboard ou une page appropriée
    } catch (error) {
      console.error("Erreur lors de la mise à jour du profil :", error);
      alert("Erreur lors de la mise à jour du profil.");
    }
  };

  return (
    <div className="modifier-profil-container">
      <h1>Modifier mon profil</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nom d'utilisateur :</label>
          <input
            type="text"
            name="username"
            value={profile.username}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Email :</label>
          <input
            type="email"
            name="email"
            value={profile.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Nouveau mot de passe :</label>
          <input
            type="password"
            name="password"
            value={profile.password}
            onChange={handleChange}
            placeholder="Laissez vide pour ne pas changer"
          />
        </div>
        <button type="submit">Mettre à jour</button>
      </form>
    </div>
  );
}

export default ModifierProfil;
