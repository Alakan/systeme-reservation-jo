import React, { useState, useEffect } from "react";
import { useParams, useNavigate }      from "react-router-dom";
import api                              from "../services/api";
import "../styles/ModifierEvenement.css";

export default function ModifierEvenement() {
  const { id }      = useParams();
  const navigate    = useNavigate();
  const [form, setForm] = useState({
    titre: "",
    description: "",
    dateEvenement: "",
    lieu: ""
  });
  const [file, setFile] = useState(null);

  useEffect(() => {
    api
      .get(`/admin/evenements/${id}`)
      .then(res => {
        const ev = res.data;
        setForm({
          titre: ev.titre,
          description: ev.description,
          dateEvenement: ev.dateEvenement.slice(0, 16),
          lieu: ev.lieu
        });
      })
      .catch(() => alert("Erreur lors du chargement"));
  }, [id]);

  const handleChange = e => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };
  const handleFile   = e => setFile(e.target.files[0] || null);

  const handleSubmit = e => {
    e.preventDefault();
    const data = new FormData();
    data.append("titre", form.titre);
    data.append("description", form.description);
    data.append("dateEvenement", form.dateEvenement.slice(0, 16));
    data.append("lieu", form.lieu);
    if (file) data.append("image", file);

    api
      .put(`/admin/evenements/${id}`, data, {
        headers: { "Content-Type": "multipart/form-data" }
      })
      .then(() => {
        alert("Événement modifié !");
        navigate("/admin");
      })
      .catch(() => alert("Erreur lors de la modification"));
  };

  return (
    <div className="form-admin">
      <h2>Modifier un événement</h2>
      <form onSubmit={handleSubmit}>
        <input
          name="titre"
          value={form.titre}
          onChange={handleChange}
          required
        />
        <textarea
          name="description"
          value={form.description}
          onChange={handleChange}
          required
        />
        <input
          type="datetime-local"
          name="dateEvenement"
          value={form.dateEvenement}
          onChange={handleChange}
          required
        />
        <input
          name="lieu"
          value={form.lieu}
          onChange={handleChange}
          required
        />
        <label>
          Nouvelle image (optionnelle) :
          <input type="file" accept="image/*" onChange={handleFile} />
        </label>

        <button type="submit">Modifier</button>
      </form>
    </div>
  );
}
