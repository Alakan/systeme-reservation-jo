// src/pages/ModifierUtilisateur.js
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/ModifierUtilisateur.css";

function ModifierUtilisateur() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState({
    username: "",
    email: "",
    password: "",
  });

  useEffect(() => {
    // Récupère les informations de l'utilisateur via l'API
    // Utilisation d'un chemin relatif sans slash initial
    api.get(`utilisateurs/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    })
      .then((res) => setUser(res.data))
      .catch((error) => {
        console.error("Erreur GET:", error);
        alert("Erreur lors du chargement de l'utilisateur.");
      });
  }, [id, navigate]);

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Mise à jour de l'utilisateur via l'endpoint admin, avec chemin relatif
      await api.put(`admin/utilisateurs/${id}`, user, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      alert("Utilisateur modifié !");
      navigate("/admin");
    } catch (error) {
      console.error("Erreur PUT:", error);
      alert("Erreur lors de la modification !");
    }
  };

  return (
    <div className="modifier-utilisateur-container">
      <h2>Modifier un utilisateur</h2>
      <form onSubmit={handleSubmit}>
        <label>Nom d'utilisateur :</label>
        <input
          type="text"
          name="username"
          value={user.username}
          onChange={handleChange}
          required
        />

        <label>Email :</label>
        <input
          type="email"
          name="email"
          value={user.email}
          onChange={handleChange}
          required
        />

        <label>Nouveau mot de passe :</label>
        <input
          type="password"
          name="password"
          placeholder="Laisser vide pour ne pas modifier"
          onChange={handleChange}
        />

        <button type="submit">Modifier</button>
      </form>
    </div>
  );
}

export default ModifierUtilisateur;
