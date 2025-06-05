// src/pages/Billet.js
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/Billet.css";

function Billet() {
  const { id } = useParams(); // Ici, id correspond à l'ID de la réservation associée
  const [billet, setBillet] = useState(null);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const formatPrice = (price) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    // Appel à l'API pour récupérer le billet associé à la réservation
    api.get(`/billets/reservation/${id}`, { headers: { Authorization: `Bearer ${token}` } })
      .then((response) => {
        setBillet(response.data);
      })
      .catch((error) => {
        console.error("Erreur lors du chargement du billet :", error);
        navigate("/404");
      });
  }, [id, token, navigate]);

  if (!billet) {
    return <div>Chargement du billet...</div>;
  }

  return (
    <div className="billet-container">
      <h1>Détails du Billet</h1>
      <div className="billet-info">
        <p>
          <strong>Numéro de billet :</strong> {billet.numeroBillet}
        </p>
        <p>
          <strong>Date de création :</strong> {new Date(billet.dateReservation).toLocaleString("fr-FR")}
        </p>
        <p>
          <strong>Statut :</strong> {billet.statut}
        </p>
        <p>
          <strong>Type de billet :</strong> {billet.type}
        </p>
        <p>
          <strong>Événement :</strong> {billet.evenement ? billet.evenement.titre : "N/A"}
        </p>
        <p>
          <strong>Lieu :</strong> {billet.evenement ? billet.evenement.lieu : "N/A"}
        </p>
        <p>
          <strong>Prix total du billet :</strong>{" "}
          {billet.prixTotal ? formatPrice(billet.prixTotal) : "Non renseigné"}
        </p>
      </div>
      <button onClick={() => navigate("/mesreservations")} className="btn-back">
        Retour à mes réservations
      </button>
    </div>
  );
}

export default Billet;
