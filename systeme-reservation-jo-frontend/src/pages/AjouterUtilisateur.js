// src/pages/AjouterUtilisateur.js
import React, { useState } from "react";
import { useNavigate }      from "react-router-dom";
import api                  from "../services/api";
import "../styles/AjouterUtilisateur.css";

export default function AjouterUtilisateur() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    roles: []      // ← on ajoute ce champ
  });
  const [error, setError]           = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  // Liste possible des rôles
  const allRoles = [
    { value: "ROLE_UTILISATEUR",    label: "Utilisateur" },
    { value: "ROLE_ADMINISTRATEUR", label: "Administrateur" }
  ];

  const handleChange = e => {
    const { name, value } = e.target;
    if (name === "role") {
      setForm(prev => ({ ...prev, roles: [value] }));
    } else {
      setForm(prev => ({ ...prev, [name]: value }));
    }
    setError("");
  };

  const handleSubmit = async e => {
    e.preventDefault();

    // validations front-end
    if (form.username.trim().length < 3) {
      return setError("Le nom d’utilisateur doit contenir au moins 3 caractères.");
    }
    if (!/^\S+@\S+\.\S+$/.test(form.email)) {
      return setError("Merci de saisir un email valide.");
    }
    if (form.password.length < 6) {
      return setError("Le mot de passe doit contenir au moins 6 caractères.");
    }
    if (form.roles.length === 0) {
      return setError("Veuillez choisir un rôle pour l’utilisateur.");
    }

    setIsSubmitting(true);
    try {
      const token = localStorage.getItem("token");
      await api.post(
        "/admin/utilisateurs",
        form,  // username, email, password, roles
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );
      navigate("/admin");
    } catch (err) {
      setError(
        err.response?.data ||
        "Erreur lors de l'ajout de l'utilisateur."
      );
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

        <label htmlFor="role">Rôle</label>
        <select
          id="role"
          name="role"
          value={form.roles[0] || ""}
          onChange={handleChange}
          required
        >
          <option value="">-- Choisir un rôle --</option>
          {allRoles.map(r => (
            <option key={r.value} value={r.value}>
              {r.label}
            </option>
          ))}
        </select>

        {error && <p className="error-msg">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Ajout en cours..." : "Ajouter"}
        </button>
      </form>
    </div>
  );
}
