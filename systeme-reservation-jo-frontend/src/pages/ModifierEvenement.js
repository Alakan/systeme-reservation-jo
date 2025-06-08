// src/pages/ModifierEvenement.js
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/ModifierEvenement.css";

function ModifierEvenement() {
  const { id } = useParams(); // Récupère l'ID de l'événement sélectionné
  const navigate = useNavigate();
  const [evenement, setEvenement] = useState({
    titre: "",
    description: "",
    dateEvenement: "",
    lieu: "",
  });

  // Récupérer les infos de l'événement via l'API
  useEffect(() => {
    api.get(`admin/evenements/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    })
      .then((res) => setEvenement(res.data))
      .catch(() => alert("Erreur lors du chargement de l'événement."));
  }, [id]);

  const handleChange = (e) => {
    setEvenement({ ...evenement, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      api.put(`admin/evenements/${id}`, evenement, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      })
      .then(() => {
        alert("Événement modifié !");
        navigate("/admin");
      })
      .catch(() => {
        alert("Erreur lors de la modification !");
      });
    } catch (error) {
      alert("Erreur lors de la modification !");
    }
  };

  return (
    <div className="modifier-evenement-container">
      <h2>Modifier un événement</h2>
      <form onSubmit={handleSubmit}>
        <label>Titre :</label>
        <input type="text" name="titre" value={evenement.titre} onChange={handleChange} required />

        <label>Description :</label>
        <textarea name="description" value={evenement.description} onChange={handleChange} required />

        <label>Date :</label>
        <input type="datetime-local" name="dateEvenement" value={evenement.dateEvenement} onChange={handleChange} required />

        <label>Lieu :</label>
        <input type="text" name="lieu" value={evenement.lieu} onChange={handleChange} required />

        <button type="submit">Modifier</button>
      </form>
    </div>
  );
}

export default ModifierEvenement;
