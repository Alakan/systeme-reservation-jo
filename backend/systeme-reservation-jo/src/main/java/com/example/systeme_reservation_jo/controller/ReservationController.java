package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasRole('ADMINISTRATEUR')") // ✅ Seuls les admins peuvent voir toutes les réservations
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // ✅ Seuls les utilisateurs connectés peuvent voir une réservation spécifique
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
        }
        return ResponseEntity.ok(reservationOpt.get());
    }

    @GetMapping("/utilisateur")
    @PreAuthorize("hasRole('UTILISATEUR') or hasRole('ADMINISTRATEUR')") // ✅ Autorise les utilisateurs normaux et les admins
    public ResponseEntity<?> getReservationsByUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // ✅ Récupère l'email de l'utilisateur connecté
        System.out.println("🔍 Email de l'utilisateur authentifié : " + userEmail);

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(userEmail);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable.");
        }

        List<Reservation> reservations = reservationService.findReservationsByUserEmail(userEmail);
        System.out.println("🔍 Réservations trouvées : " + reservations.size());

        if (reservations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vous n'avez aucune réservation en cours.");
        }

        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()") // ✅ Supprime la restriction par rôle
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création de la réservation : " + e.getMessage());
        }
    }

    @PutMapping("/{id}/paiement")
    @PreAuthorize("isAuthenticated()") // ✅ Supprime la restriction par rôle
    public ResponseEntity<?> effectuerPaiement(@PathVariable Long id, @RequestBody ModePaiement modePaiement) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Utilisateur authentifié : " + authentication.getName());
        System.out.println("Rôles de l'utilisateur : " + authentication.getAuthorities());

        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);

        if (reservationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
        }

        Reservation reservation = reservationOpt.get();
        System.out.println("Statut actuel de la réservation ID " + id + " : " + reservation.getStatut());

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La réservation a déjà été payée ou annulée.");
        }

        reservation.setModePaiement(modePaiement);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservationService.updateReservation(id, reservation);

        return ResponseEntity.ok("Paiement effectué avec succès !");
    }
}
