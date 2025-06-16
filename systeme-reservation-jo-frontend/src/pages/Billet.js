import React, { useState, useEffect } from "react";
import { useParams, useNavigate }      from "react-router-dom";
import api                             from "../services/api";
import "../styles/Billet.css";

export default function Billet() {
  const { id }       = useParams();
  const navigate     = useNavigate();
  const [billet, setBillet] = useState(null);
  const token        = localStorage.getItem("token");

  // formate en EUR
  const formatPrice = (price) =>
    new Intl.NumberFormat("fr-FR", {
      style: "currency",
      currency: "EUR",
    }).format(price);

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    api
      .get(`billets/reservation/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setBillet(res.data))
      .catch(() => navigate("/404"));
  }, [id, token, navigate]);

  if (!billet) {
    return <p className="billet-loading">Chargement du billet…</p>;
  }

  return (
    <div className="billet-container">
      <h1>Détails du Billet</h1>

      <dl className="billet-details">
        <dt>Numéro de billet :</dt>
        <dd>{billet.numeroBillet}</dd>

        <dt>Date de création :</dt>
        <dd>{new Date(billet.dateReservation).toLocaleString("fr-FR")}</dd>

        <dt>Statut :</dt>
        <dd>{billet.statut}</dd>

        <dt>Type de billet :</dt>
        <dd>{billet.type}</dd>

        <dt>Événement :</dt>
        <dd>{billet.evenement?.titre || "N/A"}</dd>

        <dt>Lieu :</dt>
        <dd>{billet.evenement?.lieu || "N/A"}</dd>

        <dt>Prix total du billet :</dt>
        <dd>
          {billet.prixTotal != null
            ? formatPrice(billet.prixTotal)
            : "Non renseigné"}
        </dd>
      </dl>

      <button
        className="billet-back"
        onClick={() => navigate("/mes-reservations")}
      >
        ← Retour à mes réservations
      </button>
    </div>
  );
}
