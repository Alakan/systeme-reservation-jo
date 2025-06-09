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
      // 🔹 Correction : Ajout des secondes si absentes
      let formattedDate = evenement.dateEvenement;
      if (formattedDate.length === 16) { 
        formattedDate += ":00"; 
      } // Si l'utilisateur a saisi HH:mm, on ajoute ":00"

      // 🔹 Conversion au format ISO si nécessaire
      const dateISO = new Date(formattedDate).toISOString().slice(0, 19);

      await api.post("evenements", { ...evenement, dateEvenement: dateISO }, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });

      alert("Événement ajouté avec succès !");
      navigate("/admin");
    } catch (error) {
      console.error("Erreur lors de l'ajout de l'événement :", error);
      alert("Erreur lors de l'ajout de l'événement ! Vérifiez la date au format : yyyy-MM-dd HH:mm:ss");
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
          value={evenement.titre}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="description"
          placeholder="Description"
          value={evenement.description}
          onChange={handleChange}
          required
        />
        <input
          type="datetime-local"
          name="dateEvenement"
          value={evenement.dateEvenement}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="lieu"
          placeholder="Lieu"
          value={evenement.lieu}
          onChange={handleChange}
          required
        />
        <button type="submit">Ajouter</button>
      </form>
    </div>
  );
}

export default AjouterEvenement;
