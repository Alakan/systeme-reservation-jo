// src/pages/ModifierEvenement.js
import React, { useState, useEffect } from "react";
import { useParams, useNavigate }      from "react-router-dom";
import api                              from "../services/api";
import "../styles/ModifierEvenement.css";

export default function ModifierEvenement() {
  const { id }       = useParams();
  const navigate     = useNavigate();
  const [form, setForm] = useState({
    titre: "",
    description: "",
    dateEvenement: "",
    lieu: ""
  });
  const [file, setFile]         = useState(null);
  const [error, setError]       = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    api.get(`/admin/evenements/${id}`)
      .then(res => {
        const ev = res.data;
        setForm({
          titre: ev.titre,
          description: ev.description,
          dateEvenement: ev.dateEvenement.slice(0, 16),
          lieu: ev.lieu
        });
      })
      .catch(() => setError("Erreur lors du chargement de l’événement."));
  }, [id]);

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
    // validations front-end
    if (form.titre.trim().length < 5) {
      setError("Le titre doit contenir au moins 5 caractères.");
      return;
    }
    if (form.description.trim().length < 10) {
      setError("La description doit contenir au moins 10 caractères.");
      return;
    }
    if (!form.dateEvenement) {
      setError("Veuillez sélectionner une date et une heure.");
      return;
    }
    if (form.lieu.trim().length === 0) {
      setError("Le lieu ne peut pas être vide.");
      return;
    }

    setIsSubmitting(true);
    try {
      const data = new FormData();
      data.append("titre", form.titre);
      data.append("description", form.description);
      data.append("dateEvenement", form.dateEvenement.slice(0,16));
      data.append("lieu", form.lieu);
      if (file) data.append("image", file);

      await api.put(`/admin/evenements/${id}`, data, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      navigate("/admin");
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de la modification.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modifier-evenement-container">
      <h2>Modifier un événement</h2>
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

        <label htmlFor="image">Nouvelle image (optionnelle)</label>
        <input
          id="image"
          type="file"
          accept="image/*"
          onChange={handleFile}
        />

        {error && <p className="error-msg">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Modification en cours…" : "Modifier"}
        </button>
      </form>
    </div>
  );
}
