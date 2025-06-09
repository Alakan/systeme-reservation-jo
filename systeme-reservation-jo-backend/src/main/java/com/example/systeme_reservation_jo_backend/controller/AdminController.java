package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo_backend.model.Utilisateur;
import com.example.systeme_reservation_jo_backend.model.Evenement;
import com.example.systeme_reservation_jo_backend.model.Reservation;
import com.example.systeme_reservation_jo_backend.service.UtilisateurService;
import com.example.systeme_reservation_jo_backend.service.EvenementService;
import com.example.systeme_reservation_jo_backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
@CrossOrigin(origins = {"http://localhost:3000", "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"})
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final EvenementService evenementService;
    private final ReservationService reservationService;

    public AdminController(UtilisateurService utilisateurService,
                           EvenementService evenementService,
                           ReservationService reservationService) {
        this.utilisateurService = utilisateurService;
        this.evenementService = evenementService;
        this.reservationService = reservationService;
    }

    // Récupérer tous les utilisateurs (y compris les administrateurs)
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    // Création d'un utilisateur par l'administrateur
    @PostMapping("/utilisateurs")
    public ResponseEntity<?> createUtilisateur(@Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            // Convertir le DTO en entité Utilisateur
            Utilisateur newUser = dtoToEntity(utilisateurDTO);
            // Utiliser la méthode saveUtilisateur pour enregistrer le nouvel utilisateur
            Utilisateur createdUser = utilisateurService.saveUtilisateur(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }


    // Modification d'un utilisateur par l'admin
    @PutMapping("/utilisateurs/{id}")
    public ResponseEntity<?> updateUtilisateur(@PathVariable Long id,
                                               @Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            Utilisateur utilisateurToUpdate = dtoToEntity(utilisateurDTO);
            Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(id, utilisateurToUpdate);
            return ResponseEntity.ok(updatedUtilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    // Suppression d'un utilisateur (suppression physique)
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<String> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

    // Récupérer tous les événements
    @GetMapping("/evenements")
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        return ResponseEntity.ok(evenements);
    }

    // Désactivation d'un événement (mise à jour du champ actif à false)
    @PutMapping("/evenements/{id}/desactiver")
    public ResponseEntity<?> desactiverEvenement(@PathVariable Long id) {
        try {
            Evenement evenementDesactive = evenementService.desactiverEvenement(id);
            return ResponseEntity.ok(evenementDesactive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la désactivation de l'événement : " + e.getMessage());
        }
    }

    // Réactivation d'un événement (champ actif à true)
    @PutMapping("/evenements/{id}/reactiver")
    public ResponseEntity<?> reactiverEvenement(@PathVariable Long id) {
        try {
            Evenement evenementActive = evenementService.reactiverEvenement(id);
            return ResponseEntity.ok(evenementActive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la réactivation de l'événement : " + e.getMessage());
        }
    }

    // Récupérer toutes les réservations
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Création d'une réservation via l'administration (si nécessaire)
    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création de la réservation : " + e.getMessage());
        }
    }

    // Désactivation d'une réservation (mise à jour du champ actif à false)
    @PutMapping("/reservations/{id}/desactiver")
    public ResponseEntity<?> desactiverReservation(@PathVariable Long id) {
        try {
            Reservation reservationDesactive = reservationService.desactiverReservation(id);
            return ResponseEntity.ok(reservationDesactive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la désactivation de la réservation : " + e.getMessage());
        }
    }

    // Réactivation d'une réservation (champ actif à true)
    @PutMapping("/reservations/{id}/reactiver")
    public ResponseEntity<?> reactiverReservation(@PathVariable Long id) {
        try {
            Reservation reservationActive = reservationService.reactiverReservation(id);
            return ResponseEntity.ok(reservationActive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la réactivation de la réservation : " + e.getMessage());
        }
    }

    /**
     * Méthode utilitaire pour convertir un UtilisateurDTO en Utilisateur.
     */
    private Utilisateur dtoToEntity(UtilisateurDTO dto) {
        Utilisateur user = new Utilisateur();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }
}
