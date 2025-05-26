package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Retourne la liste de toutes les réservations
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Retourne une réservation par son id
    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    // Crée une nouvelle réservation
    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // Met à jour une réservation existante
    @Override
    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));

        // Mise à jour des informations de base
        reservation.setUtilisateur(reservationDetails.getUtilisateur());
        reservation.setDateReservation(reservationDetails.getDateReservation());
        reservation.setNombreBillets(reservationDetails.getNombreBillets());
        reservation.setStatut(reservationDetails.getStatut());
        reservation.setEvenement(reservationDetails.getEvenement());

        return reservationRepository.save(reservation);
    }

    // Supprime une réservation par son identifiant
    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));
        reservationRepository.delete(reservation);
    }

    // Retourne les réservations associées à un utilisateur donné
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUtilisateur(Long utilisateurId) {
        return reservationRepository.findReservationsByUtilisateurId(utilisateurId);
    }

    // Effectue le paiement d'une réservation
    @Override
    @Transactional
    public Reservation effectuerPaiement(Long id, ModePaiement modePaiement) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation introuvable avec l'id : " + id));

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new IllegalStateException("La réservation est déjà payée ou annulée.");
        }

        // Affecte le mode de paiement et valide la réservation
        reservation.setModePaiement(modePaiement);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        return reservationRepository.save(reservation);
    }

    // Recherche les réservations par statut
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findReservationsByStatut(StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }

    // Annule une réservation
    @Override
    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation introuvable avec l'id : " + id));

        if (reservation.getStatut() == StatutReservation.CONFIRMEE) {
            throw new IllegalStateException("Impossible d'annuler une réservation déjà confirmée.");
        }

        // Change le statut en ANNULEE pour signifier l'annulation
        reservation.setStatut(StatutReservation.ANNULEE);
        return reservationRepository.save(reservation);
    }
}
