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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.saveReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);
        if (existingReservation.isEmpty()) {
            return ResponseEntity.notFound().build(); // Retourner 404 si la réservation n'existe pas
        }
        Reservation updatedReservation = reservationService.updateReservation(id, reservationDetails);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);
        if (existingReservation.isEmpty()) {
            return ResponseEntity.notFound().build(); // Retourner 404 si la réservation n'existe pas
        }
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Reservation>> getReservationsByUtilisateur(@PathVariable Integer utilisateurId) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + utilisateurId));
        List<Reservation> reservations = reservationService.getReservationsByUtilisateur(utilisateur);
        return ResponseEntity.ok(reservations);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}