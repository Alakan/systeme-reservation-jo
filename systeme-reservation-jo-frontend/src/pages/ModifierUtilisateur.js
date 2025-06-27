// src/pages/ModifierUtilisateur.js
import React, { useState, useEffect } from "react";
import { useParams, useNavigate }      from "react-router-dom";
import api                              from "../services/api";
import "../styles/ModifierUtilisateur.css";

export default function ModifierUtilisateur() {
  const { id }       = useParams();
  const navigate     = useNavigate();
  const [user, setUser]   = useState({
    username: "",
    email: "",
    password: ""
  });
  const [error, setError]         = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // Charge l'utilisateur au montage
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem("token");
        const res   = await api.get(
          `/admin/utilisateurs/${id}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setUser({
          username: res.data.username,
          email:    res.data.email,
          password: ""
        });
      } catch {
        setError("Erreur lors du chargement de l’utilisateur.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchUser();
  }, [id]);

  const handleChange = e => {
    setUser(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSubmit = async e => {
    e.preventDefault();
    // validations
    if (user.username.trim().length < 3) {
      setError("Le nom d’utilisateur doit contenir au moins 3 caractères.");
      return;
    }
    if (!/^\S+@\S+\.\S+$/.test(user.email)) {
      setError("Merci de saisir un email valide.");
      return;
    }
    if (user.password && user.password.length < 6) {
      setError("Le mot de passe doit contenir au moins 6 caractères.");
      return;
    }

    setIsSubmitting(true);
    try {
      const token   = localStorage.getItem("token");
      const payload = { username: user.username, email: user.email };
      if (user.password) payload.password = user.password;

      await api.put(
        `/admin/utilisateurs/${id}`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      navigate("/admin");
    } catch (err) {
      setError(err.response?.data || "Erreur lors de la modification.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="modifier-utilisateur-container">
        <p>Chargement…</p>
      </div>
    );
  }

  return (
    <div className="modifier-utilisateur-container">
      <h2>Modifier un utilisateur</h2>
      {error && <p className="error-msg">{error}</p>}
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="username">Nom d’utilisateur</label>
        <input
          id="username"
          name="username"
          value={user.username}
          onChange={handleChange}
          required
        />

        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="email"
          name="email"
          value={user.email}
          onChange={handleChange}
          required
        />

        <label htmlFor="password">Mot de passe</label>
        <input
          id="password"
          type="password"
          name="password"
          placeholder="Laisser vide pour ne pas modifier"
          value={user.password}
          onChange={handleChange}
        />

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Modification en cours…" : "Modifier"}
        </button>
      </form>
    </div>
  );
}
