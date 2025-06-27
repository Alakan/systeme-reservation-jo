// src/pages/ModifierReservation.js
import React, { useState, useEffect } from "react";
import { useParams, useNavigate }      from "react-router-dom";
import api                              from "../services/api";
import "../styles/ModifierReservation.css";

export default function ModifierReservation() {
  const { id }       = useParams();
  const navigate     = useNavigate();
  const [form, setForm] = useState({
    utilisateurId:   "",
    evenementId:     "",
    dateReservation: "",
    nombreBillets:   "",
    modePaiement:    ""
  });
  const [error, setError]         = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    api.get(`admin/reservations/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    })
    .then(res => {
      const r = res.data;
      setForm({
        utilisateurId:   r.utilisateurId,
        evenementId:     r.evenementId,
        dateReservation: r.dateReservation.slice(0,16),
        nombreBillets:   r.nombreBillets,
        modePaiement:    r.modePaiement || ""
      });
    })
    .catch(() => setError("Erreur lors du chargement de la réservation."));
  }, [id]);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    setError("");
  };

  const handleSubmit = async e => {
    e.preventDefault();
    // validations front-end
    if (!form.evenementId || Number(form.evenementId) <= 0) {
      setError("ID d’événement invalide.");
      return;
    }
    if (!form.utilisateurId || Number(form.utilisateurId) <= 0) {
      setError("ID utilisateur invalide.");
      return;
    }
    if (!form.dateReservation) {
      setError("Veuillez saisir une date de réservation.");
      return;
    }
    if (!form.nombreBillets || Number(form.nombreBillets) < 1) {
      setError("Le nombre de billets doit être au moins 1.");
      return;
    }

    setIsSubmitting(true);
    try {
      await api.put(
        `admin/reservations/${id}`,
        {
          utilisateurId:   form.utilisateurId,
          evenementId:     form.evenementId,
          dateReservation: form.dateReservation,
          nombreBillets:   form.nombreBillets,
          modePaiement:    form.modePaiement
        },
        {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
        }
      );
      navigate("/admin");
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de la modification.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modifier-reservation-container">
      <h2>Modifier une réservation</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="utilisateurId">ID Utilisateur</label>
        <input
          id="utilisateurId"
          name="utilisateurId"
          type="number"
          value={form.utilisateurId}
          onChange={handleChange}
          min="1"
          required
        />

        <label htmlFor="evenementId">ID Événement</label>
        <input
          id="evenementId"
          name="evenementId"
          type="number"
          value={form.evenementId}
          onChange={handleChange}
          min="1"
          required
        />

        <label htmlFor="dateReservation">Date & heure</label>
        <input
          id="dateReservation"
          name="dateReservation"
          type="datetime-local"
          value={form.dateReservation}
          onChange={handleChange}
          required
        />

        <label htmlFor="nombreBillets">Nombre de billets</label>
        <input
          id="nombreBillets"
          name="nombreBillets"
          type="number"
          value={form.nombreBillets}
          onChange={handleChange}
          min="1"
          required
        />

        <label htmlFor="modePaiement">Mode de paiement</label>
        <select
          id="modePaiement"
          name="modePaiement"
          value={form.modePaiement}
          onChange={handleChange}
        >
          <option value="">— Choisir —</option>
          <option value="cb">Carte bancaire</option>
          <option value="paypal">PayPal</option>
          <option value="cheque">Chèque</option>
        </select>

        {error && <p className="error-msg">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Modification en cours…" : "Modifier"}
        </button>
      </form>
    </div>
  );
}
