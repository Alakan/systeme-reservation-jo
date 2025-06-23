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
    lieu: "",
    prix: "",
    capaciteTotale: ""
  });
  const [error, setError]           = useState("");
  const [loading, setLoading]       = useState(true);
  const [isSubmitting, setSubmitting] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    api
      .get(`/admin/evenements/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then(res => {
        const ev = res.data;
        setForm({
          titre: ev.titre || "",
          description: ev.description || "",
          dateEvenement: ev.dateEvenement?.slice(0, 16) || "",
          lieu: ev.lieu || "",
          prix: ev.prix?.toString() || "",
          capaciteTotale: ev.capaciteTotale?.toString() || ""
        });
      })
      .catch(() => setError("Impossible de charger l’événement."))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleChange = e => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSubmit = async e => {
    e.preventDefault();

    // validations
    if (form.titre.trim().length < 5)
      return setError("Le titre doit contenir au moins 5 caractères.");
    if (form.description.trim().length < 10)
      return setError("La description doit contenir au moins 10 caractères.");
    if (!form.dateEvenement)
      return setError("Merci de sélectionner une date et une heure.");
    if (!form.lieu.trim())
      return setError("Le lieu ne peut pas être vide.");
    if (!form.prix || Number(form.prix) <= 0)
      return setError("Le prix doit être un nombre positif.");
    if (!form.capaciteTotale || Number(form.capaciteTotale) < 1)
      return setError("La capacité doit être au moins 1.");

    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    setSubmitting(true);
    try {
      await api.put(
        `/admin/evenements/${id}`,
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
      setError(err.response?.data || "Erreur lors de la modification.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="modifier-evenement-container">
        <p>Chargement de l’événement…</p>
      </div>
    );
  }

  return (
    <div className="modifier-evenement-container">
      <h2>Modifier un événement</h2>
      {error && <p className="error-msg">{error}</p>}

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

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Modification en cours…" : "Modifier"}
        </button>
      </form>
    </div>
  );
}
