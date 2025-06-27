// src/pages/AjouterEvenement.js
import React, { useState } from "react";
import { useNavigate }      from "react-router-dom";
import api                  from "../services/api";
import "../styles/AjouterEvenement.css";

export default function AjouterEvenement() {
  const [form, setForm] = useState({
    titre: "",
    description: "",
    dateEvenement: "",
    lieu: "",
    prix: "",
    capaciteTotale: ""
  });
  const [error, setError]         = useState("");
  const [isSubmitting, setSubmitting] = useState(false);
  const navigate                  = useNavigate();

  const handleChange = e => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSubmit = async e => {
    e.preventDefault();
    if (form.titre.trim().length < 5) {
      return setError("Le titre doit faire au moins 5 caractères.");
    }
    if (form.description.trim().length < 10) {
      return setError("La description doit faire au moins 10 caractères.");
    }
    if (!form.dateEvenement) {
      return setError("Merci de sélectionner une date et une heure.");
    }
    if (form.prix === "" || Number(form.prix) <= 0) {
      return setError("Le prix doit être un nombre positif.");
    }
    if (!form.capaciteTotale || Number(form.capaciteTotale) < 1) {
      return setError("La capacité doit être au moins 1.");
    }

    setSubmitting(true);
    try {
      const token = localStorage.getItem("token");
      await api.post(
        "/admin/evenements",
        {
          titre: form.titre,
          description: form.description,
          dateEvenement: form.dateEvenement,
          lieu: form.lieu,
          prix: parseFloat(form.prix),
          capaciteTotale: parseInt(form.capaciteTotale, 10)
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );
      navigate("/admin");
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de l'ajout de l'événement.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="ajouter-evenement-container">
      <h2>Ajouter un événement</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="titre">Titre</label>
        <input
          id="titre"
          name="titre"
          value={form.titre}
          onChange={handleChange}
          required
        />

        <label htmlFor="description">Description</label>
        <textarea
          id="description"
          name="description"
          value={form.description}
          onChange={handleChange}
          rows="4"
          required
        />

        <label htmlFor="dateEvenement">Date & heure</label>
        <input
          id="dateEvenement"
          name="dateEvenement"
          type="datetime-local"
          value={form.dateEvenement}
          onChange={handleChange}
          required
        />

        <label htmlFor="lieu">Lieu</label>
        <input
          id="lieu"
          name="lieu"
          value={form.lieu}
          onChange={handleChange}
          required
        />

        <label htmlFor="prix">Prix (€)</label>
        <input
          id="prix"
          name="prix"
          type="number"
          step="0.01"
          value={form.prix}
          onChange={handleChange}
          required
        />

        <label htmlFor="capaciteTotale">Capacité totale</label>
        <input
          id="capaciteTotale"
          name="capaciteTotale"
          type="number"
          min="1"
          value={form.capaciteTotale}
          onChange={handleChange}
          required
        />

        {error && <p className="error-msg">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Ajout en cours..." : "Ajouter"}
        </button>
      </form>
    </div>
);
}
