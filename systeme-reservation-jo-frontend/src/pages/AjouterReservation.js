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
      // Pour le champ dateReservation, si la valeur contient plus de 16 caractères,
      // on la tronque pour obtenir le format "yyyy-MM-ddTHH:mm" (par exemple "2025-06-16T22:55").
      let dateStr = reservation.dateReservation;
      if (dateStr.length > 16) {
        dateStr = dateStr.slice(0, 16);
      }
      
      // Préparation du payload en veillant à convertir les champs numériques
      const payload = {
        utilisateurId: parseInt(reservation.utilisateurId, 10),
        evenementId: parseInt(reservation.evenementId, 10),
        dateReservation: dateStr,
        nombreBillets: parseInt(reservation.nombreBillets, 10),
        modePaiement: reservation.modePaiement,
      };

      // Envoi de la requête POST sur l'endpoint "admin/reservations"
      await api.post("admin/reservations", payload, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });

      alert("Réservation ajoutée avec succès !");
      navigate("/admin");
    } catch (error) {
      console.error("Erreur lors de l'ajout de la réservation :", error);
      alert("Erreur lors de l'ajout de la réservation !");
    }
  };

  return (
    <div>
      <h2>Ajouter une réservation</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="number"
          name="utilisateurId"
          placeholder="ID utilisateur"
          onChange={handleChange}
          required
        />
        <input
          type="number"
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
          min="1"
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
