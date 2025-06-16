import React, { useState, useEffect } from "react";
import { useParams, useNavigate }      from "react-router-dom";
import api                              from "../services/api";
import "../styles/ModifierUtilisateur.css";

export default function ModifierUtilisateur() {
  const { id }      = useParams();
  const navigate    = useNavigate();
  const [user, setUser] = useState({
    username: "",
    email: "",
    password: ""
  });

  useEffect(() => {
    api
      .get(`/admin/utilisateurs/${id}`)
      .then(res => {
        const u = res.data;
        setUser({ username: u.username, email: u.email, password: "" });
      })
      .catch(() => alert("Erreur chargement utilisateur"));
  }, [id]);

  const handleChange = e => {
    setUser(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      // Si password vide, on ne l’envoie pas
      const payload = { username: user.username, email: user.email };
      if (user.password) payload.password = user.password;

      await api.put(`/admin/utilisateurs/${id}`, payload);
      alert("Utilisateur modifié !");
      navigate("/admin");
    } catch {
      alert("Erreur lors de la modification");
    }
  };

  return (
    <div className="form-admin">
      <h2>Modifier un utilisateur</h2>
      <form onSubmit={handleSubmit}>
        <input
          name="username"
          value={user.username}
          onChange={handleChange}
          required
        />
        <input
          type="email"
          name="email"
          value={user.email}
          onChange={handleChange}
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Laisser vide pour ne pas modifier"
          onChange={handleChange}
        />
        <button type="submit">Modifier</button>
      </form>
    </div>
  );
}
