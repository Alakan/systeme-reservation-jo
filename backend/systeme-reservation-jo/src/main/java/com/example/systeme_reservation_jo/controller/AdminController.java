package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.example.systeme_reservation_jo.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
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

    // Modification d'un utilisateur par l'admin
    @PutMapping("/utilisateurs/{id}")
    public ResponseEntity<?> updateUtilisateur(@PathVariable Long id,
                                               @Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            // Conversion du DTO en entité
            Utilisateur utilisateurToUpdate = dtoToEntity(utilisateurDTO);
            Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(id, utilisateurToUpdate);
            return ResponseEntity.ok(updatedUtilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    // Suppression d'un utilisateur
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

    // Suppression d'un événement
    @DeleteMapping("/evenements/{id}")
    public ResponseEntity<String> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.ok("Événement supprimé avec succès.");
    }

    // Récupérer toutes les réservations
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Suppression d'une réservation
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("Réservation supprimée avec succès.");
    }

    /**
     * Méthode utilitaire pour convertir un UtilisateurDTO en Utilisateur.
     */
    private Utilisateur dtoToEntity(UtilisateurDTO dto) {
        Utilisateur user = new Utilisateur();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Le mot de passe est transmis si présent (le service se chargera de l'encoder)
        user.setPassword(dto.getPassword());
        return user;
    }
}
