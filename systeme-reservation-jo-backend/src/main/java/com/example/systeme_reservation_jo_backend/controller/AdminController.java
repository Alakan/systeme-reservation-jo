// src/main/java/com/example/systeme_reservation_jo_backend/controller/AdminController.java
package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.dto.EvenementDTO;
import com.example.systeme_reservation_jo_backend.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo_backend.model.Evenement;
import com.example.systeme_reservation_jo_backend.model.Reservation;
import com.example.systeme_reservation_jo_backend.model.Role;
import com.example.systeme_reservation_jo_backend.model.Utilisateur;
import com.example.systeme_reservation_jo_backend.repository.RoleRepository;
import com.example.systeme_reservation_jo_backend.service.EvenementService;
import com.example.systeme_reservation_jo_backend.service.ReservationService;
import com.example.systeme_reservation_jo_backend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
})
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final RoleRepository      roleRepository;
    private final EvenementService    evenementService;
    private final ReservationService  reservationService;

    public AdminController(UtilisateurService utilisateurService,
                           RoleRepository      roleRepository,
                           EvenementService    evenementService,
                           ReservationService  reservationService) {
        this.utilisateurService = utilisateurService;
        this.roleRepository     = roleRepository;
        this.evenementService   = evenementService;
        this.reservationService  = reservationService;
    }

    // --- UTILISATEURS ----------------------------------------------------

    @GetMapping("/utilisateurs")
    public ResponseEntity<?> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @GetMapping("/utilisateurs/{id}")
    public ResponseEntity<?> getUtilisateurById(@PathVariable Long id) {
        return utilisateurService.getUtilisateurById(id)
                .map(u -> {
                    UtilisateurDTO dto = new UtilisateurDTO();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setEmail(u.getEmail());
                    // ne pas renvoyer le password
                    dto.setRoles(
                            u.getRoles().stream()
                                    .map(Role::getName)
                                    .collect(Collectors.toSet())
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/utilisateurs")
    public ResponseEntity<?> createUtilisateur(@Valid @RequestBody UtilisateurDTO dto) {
        try {
            Utilisateur u = new Utilisateur();
            u.setUsername(dto.getUsername());
            u.setEmail(dto.getEmail());
            u.setPassword(dto.getPassword());

            // récupération et association des rôles
            Set<Role> roles = dto.getRoles().stream()
                    .map(name -> roleRepository.findByName(name)
                            .orElseThrow(() ->
                                    new RuntimeException("Rôle introuvable : " + name)))
                    .collect(Collectors.toSet());
            u.setRoles(roles);

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

            // mise à jour des rôles
            Set<Role> roles = dto.getRoles().stream()
                    .map(name -> roleRepository.findByName(name)
                            .orElseThrow(() ->
                                    new RuntimeException("Rôle introuvable : " + name)))
                    .collect(Collectors.toSet());
            u.setRoles(roles);

            Utilisateur updated = utilisateurService.updateUtilisateur(id, u);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Erreur mise à jour utilisateur : " + e.getMessage());
        }
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

    // --- EVENEMENTS ------------------------------------------------------

    @GetMapping("/evenements")
    public ResponseEntity<?> getAllEvenements() {
        return ResponseEntity.ok(evenementService.getAllEvenements());
    }

    @GetMapping("/evenements/{id}")
    public ResponseEntity<?> getEvenementById(@PathVariable Long id) {
        return evenementService.getEvenementById(id)
                .map(ev -> {
                    EvenementDTO dto = new EvenementDTO();
                    dto.setId(ev.getId());
                    dto.setTitre(ev.getTitre());
                    dto.setDescription(ev.getDescription());
                    dto.setDateEvenement(ev.getDateEvenement());
                    dto.setLieu(ev.getLieu());
                    dto.setPrix(ev.getPrix());
                    dto.setCapaciteTotale(ev.getCapaciteTotale());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<?> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
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
