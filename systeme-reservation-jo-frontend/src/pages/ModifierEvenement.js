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
  const [file, setFile]             = useState(null);
  const [error, setError]           = useState("");
  const [loading, setLoading]       = useState(true);
  const [isSubmitting, setSubmitting] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Vous devez être connecté·e");
      return navigate("/login");
    }

    api
      .get(`/admin/evenements/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then(res => {
        console.log("données reçues pour modification :", res.data);
        const ev = res.data;
        setForm({
          titre: ev.titre || "",
          description: ev.description || "",
          dateEvenement: ev.dateEvenement
            ? ev.dateEvenement.slice(0, 16)
            : "",
          lieu: ev.lieu || ""
        });
      })
      .catch(err => {
        console.error("Erreur chargement événement :", err);
        setError("Impossible de charger l’événement.");
      })
      .finally(() => setLoading(false));
  }, [id, navigate]);

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
    // ==== validations ====
    if (form.titre.trim().length < 5) {
      return setError("Le titre doit contenir au moins 5 caractères.");
    }
    if (form.description.trim().length < 10) {
      return setError("La description doit contenir au moins 10 caractères.");
    }
    if (!form.dateEvenement) {
      return setError("Veuillez sélectionner une date et une heure.");
    }
    if (form.lieu.trim() === "") {
      return setError("Le lieu ne peut pas être vide.");
    }

    const token = localStorage.getItem("token");
    if (!token) {
      alert("Vous devez être connecté·e");
      return navigate("/login");
    }

    setSubmitting(true);
    try {
      const data = new FormData();
      data.append("titre", form.titre);
      data.append("description", form.description);
      data.append("dateEvenement", form.dateEvenement);
      data.append("lieu", form.lieu);
      if (file) data.append("image", file);

      await api.put(`/admin/evenements/${id}`, data, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data"
        }
      });
      navigate("/admin");
    } catch (err) {
      console.error("Erreur mise à jour événement :", err);
      setError(
        err.response?.data?.message ||
        "Erreur lors de la modification de l’événement."
      );
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
