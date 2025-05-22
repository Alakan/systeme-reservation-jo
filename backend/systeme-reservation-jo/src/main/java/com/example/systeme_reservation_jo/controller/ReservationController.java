package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_UTILISATEUR', 'ROLE_ADMINISTRATEUR')")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_UTILISATEUR', 'ROLE_ADMINISTRATEUR')")
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            if (reservation.getEvenement() == null || reservation.getUtilisateur() == null) {
                return ResponseEntity.badRequest().body("L'événement et l'utilisateur sont obligatoires.");
            }
            if (reservation.getDateReservation() == null) {
                reservation.setDateReservation(LocalDateTime.now());
            }
            if (reservation.getNombreBillets() <= 0) {
                return ResponseEntity.badRequest().body("Le nombre de billets doit être supérieur à zéro.");
            }

            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(reservation.getUtilisateur().getEmail());
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
            }

            reservation.setUtilisateur(utilisateurOpt.get());
            reservation.setStatut(StatutReservation.EN_ATTENTE);

            Reservation savedReservation = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/paiement")
    @PreAuthorize("hasAnyAuthority('ROLE_UTILISATEUR', 'ROLE_ADMINISTRATEUR')")
    public ResponseEntity<?> effectuerPaiement(@PathVariable Long id, @RequestBody ModePaiement modePaiement) {
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);

        if (reservationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reservation reservation = reservationOpt.get();

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            return ResponseEntity.badRequest().body("La réservation a déjà été payée ou annulée.");
        }

        reservation.setModePaiement(modePaiement);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservationService.updateReservation(id, reservation);

        return ResponseEntity.ok("Paiement effectué avec succès !");
    }
}
