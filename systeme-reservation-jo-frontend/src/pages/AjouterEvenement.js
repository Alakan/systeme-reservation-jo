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
  const [file, setFile] = useState(null);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleChange = e => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleFile = e => {
    setFile(e.target.files[0] || null);
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
    if (form.prix === "" || Number(form.prix) < 0) {
      return setError("Le prix doit être un nombre positif.");
    }
    if (!form.capaciteTotale || Number(form.capaciteTotale) < 1) {
      return setError("La capacité doit être au moins 1.");
    }

    setIsSubmitting(true);
    try {
      const data = new FormData();
      const dateStr = form.dateEvenement.slice(0, 16);
      data.append("titre", form.titre);
      data.append("description", form.description);
      data.append("dateEvenement", dateStr);
      data.append("lieu", form.lieu);
      data.append("prix", form.prix);
      data.append("capaciteTotale", form.capaciteTotale);
      if (file) data.append("image", file);

      const token = localStorage.getItem("token");
      await api.post("/admin/evenements", data, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data"
        }
      });

      navigate("/admin");
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de l'ajout de l'événement.");
    } finally {
      setIsSubmitting(false);
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
          placeholder="Titre de l'événement"
          required
        />

        <label htmlFor="description">Description</label>
        <textarea
          id="description"
          name="description"
          value={form.description}
          onChange={handleChange}
          placeholder="Description détaillée"
          rows="4"
          required
        />

        <label htmlFor="dateEvenement">Date & heure</label>
        <input
          id="dateEvenement"
          type="datetime-local"
          name="dateEvenement"
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
          placeholder="Lieu"
          required
        />

        <label htmlFor="prix">Prix (€)</label>
        <input
          id="prix"
          type="number"
          name="prix"
          step="0.01"
          value={form.prix}
          onChange={handleChange}
          placeholder="0.00"
          required
        />

        <label htmlFor="capaciteTotale">Capacité totale</label>
        <input
          id="capaciteTotale"
          type="number"
          name="capaciteTotale"
          value={form.capaciteTotale}
          onChange={handleChange}
          placeholder="Nombre de places"
          min="1"
          required
        />

        <label htmlFor="image">Image (optionnelle)</label>
        <input
          id="image"
          type="file"
          accept="image/*"
          onChange={handleFile}
        />

        {error && <p className="error-msg">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Ajout en cours..." : "Ajouter"}
        </button>
      </form>
    </div>
  );
}
