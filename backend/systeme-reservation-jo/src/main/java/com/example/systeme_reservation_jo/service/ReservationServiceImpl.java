package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.*;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EvenementService evenementService;

    @Override
    public Reservation saveReservation(Reservation reservation) {
        Integer evenementId = reservation.getEvenementId();
        Optional<Evenement> evenementOptional = evenementService.getEvenementById(evenementId);

        if (evenementOptional.isEmpty()) {
            throw new EntityNotFoundException("Événement non trouvé avec l'id : " + evenementId);
        }

        Evenement evenement = evenementOptional.get();
        int nombreBilletsReserves = reservation.getNombreBillets();

        if (evenement.getPlacesRestantes() < nombreBilletsReserves) {
            throw new IllegalStateException("Pas assez de places disponibles pour l'événement : " + evenement.getTitre());
        }

        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.EN_ATTENTE); // Définir un statut initial

        Reservation savedReservation = reservationRepository.save(reservation);

        // Créer les billets associés
        for (int i = 0; i < nombreBilletsReserves; i++) {
            Billet billet = new Billet();
            billet.setReservation(savedReservation);
            billet.setEvenement(evenement); // Utiliser l'objet Evenement
            // Attribuer un type de billet (vous devrez peut-être le gérer différemment)
            billet.setType(TypeBillet.ADULTE);
            // Générer un numéro de billet unique
            billet.setNumeroBillet(generateUniqueBilletNumber());
            savedReservation.addBillet(billet); // Utiliser la méthode utilitaire
        }

        // Mettre à jour le nombre de places restantes dans l'événement
        evenement.setPlacesRestantes(evenement.getPlacesRestantes() - nombreBilletsReserves);
        evenementService.updateEvenement(evenementId, evenement);

        return savedReservation;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvé avec l'id : " + id));

        reservation.setUtilisateur(reservationDetails.getUtilisateur());
        reservation.setEvenementId(reservationDetails.getEvenementId());
        // Attention à la gestion des billets!
        reservation.setDateReservation(reservationDetails.getDateReservation());
        reservation.setNombreBillets(reservationDetails.getNombreBillets());
        reservation.setStatut(reservationDetails.getStatut());
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Réservation non trouvée avec l'id : " + id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public List<Reservation> getReservationsByUtilisateur(Utilisateur utilisateur) {
        return reservationRepository.findByUtilisateur(utilisateur);
    }

    // Méthode utilitaire pour générer un numéro de billet unique
    private String generateUniqueBilletNumber() {
        return UUID.randomUUID().toString();
    }
}