import React, { useState } from "react";
import { useNavigate }      from "react-router-dom";
import api                  from "../services/api";

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
  const navigate        = useNavigate();

  const handleChange = e => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };
  const handleFile = e => {
    setFile(e.target.files[0] || null);
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      // Prépare l'objet FormData
      const data = new FormData();
      // tronque la date locale au format yyyy-MM-ddTHH:mm
      let dateStr = form.dateEvenement.slice(0, 16);
      data.append("titre", form.titre);
      data.append("description", form.description);
      data.append("dateEvenement", dateStr);
      data.append("lieu", form.lieu);
      data.append("prix", form.prix);
      data.append("capaciteTotale", form.capaciteTotale);
      if (file) data.append("image", file);

      await api.post("/admin/evenements", data, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      alert("Événement ajouté avec succès !");
      navigate("/admin");
    } catch (err) {
      console.error(err);
      alert("Erreur lors de l'ajout de l'événement.");
    }
  };

  return (
    <div className="form-admin">
      <h2>Ajouter un événement</h2>
      <form onSubmit={handleSubmit}>
        <input name="titre"        placeholder="Titre"         onChange={handleChange} required />
        <textarea
          name="description"
          placeholder="Description"
          onChange={handleChange}
          required
        />
        <input
          type="datetime-local"
          name="dateEvenement"
          onChange={handleChange}
          required
        />
        <input name="lieu"         placeholder="Lieu"          onChange={handleChange} required />
        <input
          type="number"
          name="prix"
          step="0.01"
          placeholder="Prix (€)"
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="capaciteTotale"
          placeholder="Capacité totale"
          min="1"
          onChange={handleChange}
          required
        />

        <label>
          Image (optionnelle) :
          <input type="file" accept="image/*" onChange={handleFile} />
        </label>

        <button type="submit">Ajouter</button>
      </form>
    </div>
  );
}
