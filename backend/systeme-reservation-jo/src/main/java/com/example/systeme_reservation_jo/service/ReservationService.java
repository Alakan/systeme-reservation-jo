package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    /**
     * Retourne la liste de toutes les réservations.
     */
    List<Reservation> getAllReservations();

    /**
     * Retourne une réservation par son identifiant.
     * @param id l'identifiant de la réservation
     * @return une Optional contenant la réservation si elle existe, sinon vide
     * @throws EntityNotFoundException si la réservation n'est pas trouvée (selon la logique de l'implémentation)
     */
    Optional<Reservation> getReservationById(Long id) throws EntityNotFoundException;

    /**
     * Crée une nouvelle réservation.
     */
    Reservation createReservation(Reservation reservation);

    /**
     * Met à jour une réservation existante.
     * @throws EntityNotFoundException si la réservation n'est pas trouvée
     */
    Reservation updateReservation(Long id, Reservation reservationDetails) throws EntityNotFoundException;

    /**
     * Supprime une réservation par son identifiant.
     * @throws EntityNotFoundException si la réservation n'est pas trouvée
     */
    void deleteReservation(Long id) throws EntityNotFoundException;

    /**
     * Retourne toutes les réservations associées à un utilisateur.
     */
    List<Reservation> getReservationsByUtilisateur(Long utilisateurId);

    /**
     * Effectue le paiement d'une réservation.
     * Change le statut de la réservation d'EN_ATTENTE à CONFIRMEE.
     * @throws EntityNotFoundException si la réservation n'est pas trouvée
     * @throws IllegalStateException si la réservation n'est pas en état d'attente
     */
    Reservation effectuerPaiement(Long id, ModePaiement modePaiement) throws EntityNotFoundException, IllegalStateException;

    /**
     * Recherche les réservations correspondant à un statut donné.
     */
    List<Reservation> findReservationsByStatut(StatutReservation statut);

    /**
     * Annule une réservation en changeant son statut en ANNULEE.
     * @throws EntityNotFoundException si la réservation n'est pas trouvée
     * @throws IllegalStateException si la réservation est déjà confirmée
     */
    Reservation cancelReservation(Long id) throws EntityNotFoundException, IllegalStateException;
}
