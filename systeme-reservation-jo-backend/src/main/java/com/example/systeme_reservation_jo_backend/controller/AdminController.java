package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.dto.EvenementDTO;
import com.example.systeme_reservation_jo_backend.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo_backend.model.Evenement;
import com.example.systeme_reservation_jo_backend.model.Reservation;
import com.example.systeme_reservation_jo_backend.model.Utilisateur;
import com.example.systeme_reservation_jo_backend.service.EvenementService;
import com.example.systeme_reservation_jo_backend.service.ReservationService;
import com.example.systeme_reservation_jo_backend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
})
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final EvenementService   evenementService;
    private final ReservationService  reservationService;

    public AdminController(UtilisateurService utilisateurService,
                           EvenementService evenementService,
                           ReservationService reservationService) {
        this.utilisateurService = utilisateurService;
        this.evenementService   = evenementService;
        this.reservationService  = reservationService;
    }

    // --- UTILISATEURS ----------------------------------------------------

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> list = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/utilisateurs")
    public ResponseEntity<?> createUtilisateur(
            @Valid @RequestBody UtilisateurDTO dto) {
        try {
            Utilisateur u = new Utilisateur();
            u.setUsername(dto.getUsername());
            u.setEmail(dto.getEmail());
            u.setPassword(dto.getPassword());
            Utilisateur created = utilisateurService.saveUtilisateur(u);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur création utilisateur : " + e.getMessage());
        }
    }

    @PutMapping("/utilisateurs/{id}")
    public ResponseEntity<?> updateUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurDTO dto) {
        try {
            Utilisateur u = new Utilisateur();
            u.setUsername(dto.getUsername());
            u.setEmail(dto.getEmail());
            u.setPassword(dto.getPassword());
            Utilisateur updated = utilisateurService.updateUtilisateur(id, u);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur mise à jour utilisateur : " + e.getMessage());
        }
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<String> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

    // --- EVENEMENTS ------------------------------------------------------

    @GetMapping("/evenements")
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> list = evenementService.getAllEvenements();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/evenements")
    public ResponseEntity<?> createEvenementAdmin(
            @Valid @RequestBody EvenementDTO dto) {
        try {
            Evenement ev = new Evenement();
            ev.setTitre(dto.getTitre());
            ev.setDescription(dto.getDescription());
            ev.setDateEvenement(dto.getDateEvenement());
            ev.setLieu(dto.getLieu());
            ev.setPrix(dto.getPrix());
            ev.setCapaciteTotale(dto.getCapaciteTotale());
            Evenement created = evenementService.createEvenement(ev);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur création événement : " + e.getMessage());
        }
    }

    @PutMapping("/evenements/{id}")
    public ResponseEntity<?> updateEvenementAdmin(
            @PathVariable Long id,
            @Valid @RequestBody EvenementDTO dto) {
        try {
            Evenement ev = new Evenement();
            ev.setTitre(dto.getTitre());
            ev.setDescription(dto.getDescription());
            ev.setDateEvenement(dto.getDateEvenement());
            ev.setLieu(dto.getLieu());
            ev.setPrix(dto.getPrix());
            ev.setCapaciteTotale(dto.getCapaciteTotale());
            Evenement updated = evenementService.updateEvenement(id, ev);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur mise à jour événement : " + e.getMessage());
        }
    }

    @PutMapping("/evenements/{id}/desactiver")
    public ResponseEntity<?> desactiverEvenement(@PathVariable Long id) {
        try {
            Evenement ev = evenementService.desactiverEvenement(id);
            return ResponseEntity.ok(ev);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur désactivation événement : " + e.getMessage());
        }
    }

    @PutMapping("/evenements/{id}/reactiver")
    public ResponseEntity<?> reactiverEvenement(@PathVariable Long id) {
        try {
            Evenement ev = evenementService.reactiverEvenement(id);
            return ResponseEntity.ok(ev);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur réactivation événement : " + e.getMessage());
        }
    }

    // --- RESERVATIONS ----------------------------------------------------

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> list = reservationService.getAllReservations();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservationAdmin(
            @Valid @RequestBody Reservation reservation) {
        try {
            Reservation created = reservationService.createReservation(reservation);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur création réservation : " + e.getMessage());
        }
    }

    @PutMapping("/reservations/{id}/desactiver")
    public ResponseEntity<?> desactiverReservation(@PathVariable Long id) {
        try {
            Reservation r = reservationService.desactiverReservation(id);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur désactivation réservation : " + e.getMessage());
        }
    }

    @PutMapping("/reservations/{id}/reactiver")
    public ResponseEntity<?> reactiverReservation(@PathVariable Long id) {
        try {
            Reservation r = reservationService.reactiverReservation(id);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur réactivation réservation : " + e.getMessage());
        }
    }
}