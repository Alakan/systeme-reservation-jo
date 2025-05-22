package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Reservation;
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
    @PreAuthorize("hasAnyRole('UTILISATEUR', 'ADMINISTRATEUR')")
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
    @PreAuthorize("hasAnyRole('UTILISATEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            if (reservation.getEvenement() == null) {
                return ResponseEntity.badRequest().body("L'événement lié à la réservation est obligatoire.");
            }
            if (reservation.getDateReservation() == null) {
                reservation.setDateReservation(LocalDateTime.now());
            }
            if (reservation.getNombreBillets() <= 0) {
                return ResponseEntity.badRequest().body("Le nombre de billets doit être supérieur à zéro.");
            }
            if (reservation.getUtilisateur() == null || reservation.getUtilisateur().getEmail() == null) {
                return ResponseEntity.badRequest().body("L'utilisateur lié à la réservation est obligatoire.");
            }

            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(reservation.getUtilisateur().getEmail()); // ✅ Récupération de l'utilisateur
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
            }

            reservation.setUtilisateur(utilisateurOpt.get());
            Reservation savedReservation = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        try {
            return reservationService.getReservationById(id)
                    .map(existing -> ResponseEntity.ok(reservationService.updateReservation(id, reservationDetails)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
