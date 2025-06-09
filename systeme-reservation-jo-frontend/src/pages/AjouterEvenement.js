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
    prix: "",          // champ pour le prix
    capaciteTotale: "" // champ pour la capacité totale
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setEvenement({ ...evenement, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Si la valeur de dateEvenement dépasse 16 caractères, on la tronque pour obtenir le format "yyyy-MM-ddTHH:mm"
      let dateStr = evenement.dateEvenement;
      if (dateStr.length > 16) {
        dateStr = dateStr.slice(0, 16);
      }
      // Envoi de la requête POST avec l'ensemble des champs
      await api.post(
        "evenements",
        { ...evenement, dateEvenement: dateStr },
        {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        }
      );
      alert("Événement ajouté avec succès !");
      navigate("/admin");
    } catch (error) {
      console.error("Erreur lors de l'ajout de l'événement :", error);
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
        <input
          type="number"
          step="0.01"
          name="prix"
          placeholder="Prix"
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="capaciteTotale"
          placeholder="Capacité totale"
          onChange={handleChange}
          required
          min="1"
        />
        <button type="submit">Ajouter</button>
      </form>
    </div>
  );
}

export default AjouterEvenement;
