package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.example.systeme_reservation_jo.service.ReservationService;
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

    public AdminController(UtilisateurService utilisateurService, EvenementService evenementService, ReservationService reservationService) {
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

    // Récupérer uniquement les administrateurs
    @GetMapping("/administrateurs")
    public ResponseEntity<List<Utilisateur>> getAllAdministrateurs() {
        List<Utilisateur> admins = utilisateurService.getAllAdminUsers();
        return ResponseEntity.ok(admins);
    }

    // Supprimer un utilisateur
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

    // Supprimer un événement
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

    // Supprimer une réservation
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("Réservation supprimée avec succès.");
    }
}
