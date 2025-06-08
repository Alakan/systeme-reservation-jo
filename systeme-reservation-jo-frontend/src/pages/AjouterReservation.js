// src/pages/AjouterReservation.js
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function AjouterReservation() {
  const [reservation, setReservation] = useState({
    utilisateurId: "",
    evenementId: "",
    dateReservation: "",
    nombreBillets: "",
    modePaiement: "",
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setReservation({ ...reservation, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Modification : chemin relatif sans slash initial ("admin/reservations")
      await api.post("admin/reservations", reservation, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      alert("Réservation ajoutée avec succès !");
      navigate("/admin");
    } catch (error) {
      alert("Erreur lors de l'ajout de la réservation !");
    }
  };

  return (
    <div>
      <h2>Ajouter une réservation</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="utilisateurId"
          placeholder="ID utilisateur"
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="evenementId"
          placeholder="ID événement"
          onChange={handleChange}
          required
        />
        <input
          type="datetime-local"
          name="dateReservation"
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="nombreBillets"
          placeholder="Nombre de billets"
          onChange={handleChange}
          required
        />
        <select name="modePaiement" onChange={handleChange} required>
          <option value="">Sélectionner un mode de paiement</option>
          <option value="CARTE">Carte</option>
          <option value="PAYPAL">PayPal</option>
          <option value="VIREMENT">Virement</option>
        </select>
        <button type="submit">Ajouter</button>
      </form>
    </div>
  );
}

export default AjouterReservation;
