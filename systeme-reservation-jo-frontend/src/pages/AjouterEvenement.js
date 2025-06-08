// src/pages/AjouterEvenement.js
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function AjouterEvenement() {
  const [evenement, setEvenement] = useState({
    titre: "",
    description: "",
    dateEvenement: "",
    lieu: "",
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setEvenement({ ...evenement, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Utilisation d'un chemin relatif ("admin/evenements") sans le slash initial
      await api.post("admin/evenements", evenement, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      alert("Événement ajouté avec succès !");
      navigate("/admin");
    } catch (error) {
      alert("Erreur lors de l'ajout de l'événement !");
    }
  };

  return (
    <div>
      <h2>Ajouter un événement</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="titre"
          placeholder="Titre"
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="description"
          placeholder="Description"
          onChange={handleChange}
          required
        />
        <input
          type="datetime-local"
          name="dateEvenement"
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="lieu"
          placeholder="Lieu"
          onChange={handleChange}
          required
        />
        <button type="submit">Ajouter</button>
      </form>
    </div>
  );
}

export default AjouterEvenement;
