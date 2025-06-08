// src/pages/ModifierReservation.js
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/ModifierReservation.css";

function ModifierReservation() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [reservation, setReservation] = useState({
    utilisateurId: "",
    evenementId: "",
    dateReservation: "",
    nombreBillets: "",
    modePaiement: "",
  });

  useEffect(() => {
    api.get(`admin/reservations/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    })
      .then((res) => setReservation(res.data))
      .catch(() => alert("Erreur lors du chargement de la réservation."));
  }, [id]);

  const handleChange = (e) => {
    setReservation({ ...reservation, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      api.put(`admin/reservations/${id}`, reservation, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      })
      .then(() => {
        alert("Réservation modifiée !");
        navigate("/admin");
      })
      .catch(() => alert("Erreur lors de la modification !"));
    } catch (error) {
      alert("Erreur lors de la modification !");
    }
  };

  return (
    <div className="modifier-reservation-container">
      <h2>Modifier une réservation</h2>
      <form onSubmit={handleSubmit}>
        <label>ID Utilisateur :</label>
        <input type="text" name="utilisateurId" value={reservation.utilisateurId} onChange={handleChange} required />

        <label>ID Événement :</label>
        <input type="text" name="evenementId" value={reservation.evenementId} onChange={handleChange} required />

        <label>Date :</label>
        <input type="datetime-local" name="dateReservation" value={reservation.dateReservation} onChange={handleChange} required />

        <label>Nombre de billets :</label>
        <input type="number" name="nombreBillets" value={reservation.nombreBillets} onChange={handleChange} required />

        <button type="submit">Modifier</button>
      </form>
    </div>
  );
}

export default ModifierReservation;
