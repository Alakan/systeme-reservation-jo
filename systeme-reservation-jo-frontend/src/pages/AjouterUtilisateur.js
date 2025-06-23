// src/pages/AjouterUtilisateur.js
import React, { useState } from "react";
import { useNavigate }      from "react-router-dom";
import api                  from "../services/api";
import "../styles/AjouterUtilisateur.css";

export default function AjouterUtilisateur() {
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleChange = e => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSubmit = async e => {
    e.preventDefault();

    // validations front-end
    if (form.username.trim().length < 3) {
      return setError("Le nom d'utilisateur doit contenir au moins 3 caractères.");
    }
    if (!/^\S+@\S+\.\S+$/.test(form.email)) {
      return setError("Merci de saisir un email valide.");
    }
    if (form.password.length < 6) {
      return setError("Le mot de passe doit contenir au moins 6 caractères.");
    }

    setIsSubmitting(true);
    try {
      const token = localStorage.getItem("token");
      await api.post("/admin/utilisateurs", form, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });
      navigate("/admin");
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de l'ajout de l'utilisateur.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="ajouter-utilisateur-container">
      <h2>Ajouter un utilisateur</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="username">Nom d’utilisateur</label>
        <input
          id="username"
          name="username"
          value={form.username}
          onChange={handleChange}
          placeholder="Entrez le nom d’utilisateur"
          required
        />

        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          placeholder="exemple@domaine.com"
          required
        />

        <label htmlFor="password">Mot de passe</label>
        <input
          id="password"
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          placeholder="••••••••"
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
