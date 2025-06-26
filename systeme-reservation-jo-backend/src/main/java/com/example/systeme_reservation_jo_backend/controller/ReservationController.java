// src/main/java/com/example/systeme_reservation_jo_backend/controller/ReservationController.java
package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.dto.ReservationDTO;
import com.example.systeme_reservation_jo_backend.dto.ReservationMapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
})
public class ReservationController {

    private final ReservationService reservationService;
    private final UtilisateurService utilisateurService;
    private final EvenementService   evenementService;

    public ReservationController(ReservationService reservationService,
                                 UtilisateurService utilisateurService,
                                 EvenementService evenementService) {
        this.reservationService = reservationService;
        this.utilisateurService = utilisateurService;
        this.evenementService   = evenementService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> dtos = reservationService.getAllReservations()
                .stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(res -> {
                    // Si l'utilisateur n'est pas admin et réservation désactivée → 404
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMINISTRATEUR"));
                    if (!isAdmin && !res.isActif()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
                    }
                    return ResponseEntity.ok(ReservationMapper.toDTO(res));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable."));
    }

    @GetMapping("/utilisateur")
    @PreAuthorize("hasRole('UTILISATEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<?> getReservationsByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<ReservationDTO> list = utilisateurService.findByEmail(email)
                .map(u -> reservationService.getReservationsByUtilisateur(u.getId()))
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
                .stream()
                .filter(Reservation::isActif)
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune réservation active.");
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDTO dto) {
        try {
            // 1) Récupérer utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Utilisateur user = utilisateurService.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // 2) Charger l'événement
            Evenement ev = evenementService.getEvenementById(dto.getEvenement().getId())
                    .orElseThrow(() -> new RuntimeException("Événement introuvable"));

            // 3) Construire la réservation
            Reservation r = new Reservation();
            r.setUtilisateur(user);
            r.setEvenement(ev);
            r.setNombreBillets(dto.getNombreBillets());
            r.setDateReservation(
                    dto.getDateReservation() != null
                            ? dto.getDateReservation()
                            : LocalDateTime.now()
            );

            // 4) Persister et renvoyer le DTO
            Reservation saved = reservationService.createReservation(r);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ReservationMapper.toDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur création réservation : " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReservation(@PathVariable Long id,
                                               @Valid @RequestBody ReservationDTO dto) {
        try {
            Reservation updated = reservationService.updateReservation(id, ReservationMapper.fromDTO(dto));
            return ResponseEntity.ok(ReservationMapper.toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur mise à jour : " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Réservation supprimée.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur suppression : " + ex.getMessage());
        }
    }

    @PutMapping("/{id}/paiement")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> payerReservation(@PathVariable Long id,
                                              @RequestBody String modePaiement) {
        try {
            Reservation paid = reservationService.effectuerPaiement(id, null);
            return ResponseEntity.ok(ReservationMapper.toDTO(paid));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur paiement : " + ex.getMessage());
        }
    }
}
