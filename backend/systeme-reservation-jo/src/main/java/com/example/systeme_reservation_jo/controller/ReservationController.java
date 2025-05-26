package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.dto.ReservationDTO;
import com.example.systeme_reservation_jo.dto.ReservationMapper;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UtilisateurService utilisateurService;
    private final EvenementService evenementService;

    public ReservationController(ReservationService reservationService,
                                 UtilisateurService utilisateurService,
                                 EvenementService evenementService) {
        this.reservationService = reservationService;
        this.utilisateurService = utilisateurService;
        this.evenementService = evenementService;
    }

    // Accessible uniquement aux administrateurs pour récupérer toutes les réservations
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        List<ReservationDTO> dtos = reservations.stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Récupération d'une réservation par son identifiant (retourne un DTO)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isPresent()) {
            ReservationDTO dto = ReservationMapper.toDTO(reservationOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
        }
    }

    // Récupération des réservations de l'utilisateur authentifié, renvoyées au format DTO
    @GetMapping("/utilisateur")
    @PreAuthorize("hasRole('UTILISATEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Object> getReservationsByUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        System.out.println("🔍 Email de l'utilisateur authentifié : " + userEmail);

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(userEmail);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable.");
        }

        List<Reservation> reservations = reservationService.getReservationsByUtilisateur(utilisateurOpt.get().getId());
        if (reservations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vous n'avez aucune réservation.");
        }

        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservationDTOs);
    }

    // Création d'une réservation (retourne un DTO)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> createReservation(@Valid @RequestBody Reservation reservation) {
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

            // Vérification et récupération de l'utilisateur complet via son email
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(reservation.getUtilisateur().getEmail());
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
            }

            // Récupération de l'objet Evenement complet à partir de son identifiant
            Long evenementId = reservation.getEvenement().getId();
            Optional<Evenement> evenementOpt = evenementService.getEvenementById(evenementId);
            if (evenementOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Événement non trouvé.");
            }
            reservation.setEvenement(evenementOpt.get());
            reservation.setUtilisateur(utilisateurOpt.get());
            reservation.setStatut(StatutReservation.EN_ATTENTE);

            Reservation savedReservation = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(ReservationMapper.toDTO(savedReservation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création de la réservation : " + e.getMessage());
        }
    }

    // Mise à jour d'une réservation existante (retourne un DTO)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(id, reservationDetails);
            return ResponseEntity.ok(ReservationMapper.toDTO(updatedReservation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la mise à jour de la réservation : " + e.getMessage());
        }
    }

    // Suppression d'une réservation
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Réservation supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la suppression de la réservation : " + e.getMessage());
        }
    }

    // Effectuer le paiement d'une réservation en appelant le service dédié
    @PutMapping("/{id}/paiement")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> effectuerPaiement(@PathVariable Long id, @RequestBody ModePaiement modePaiement) {
        try {
            Reservation updatedReservation = reservationService.effectuerPaiement(id, modePaiement);
            return ResponseEntity.ok("Paiement effectué avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors du paiement : " + e.getMessage());
        }
    }
}
