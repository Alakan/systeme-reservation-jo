// src/pages/AjouterUtilisateur.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

function AjouterUtilisateur() {
  const [user, setUser] = useState({
    username: '',
    email: '',
    password: ''
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/utilisateurs', user, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      alert("Utilisateur ajouté avec succès !");
      navigate('/admin'); // Retour à la liste des utilisateurs
    } catch (error) {
      alert("Erreur lors de l'ajout de l'utilisateur !");
    }
  };

  return (
    <div>
      <h2>Ajouter un utilisateur</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="username" placeholder="Nom d'utilisateur" onChange={handleChange} required />
        <input type="email" name="email" placeholder="Email" onChange={handleChange} required />
        <input type="password" name="password" placeholder="Mot de passe" onChange={handleChange} required />
        <button type="submit">Ajouter</button>
      </form>
    </div>
  );
}

export default AjouterUtilisateur;
