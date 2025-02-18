package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Paiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.service.PaiementService;
import com.example.systeme_reservation_jo.service.ReservationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<Paiement> getAllPaiements() {
        return paiementService.getAllPaiements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable Long id) {
        return paiementService.getPaiementById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paiement> createPaiement(@Valid @RequestBody Paiement paiement) {
        Paiement savedPaiement = paiementService.savePaiement(paiement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPaiement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> updatePaiement(@PathVariable Long id, @Valid @RequestBody Paiement paiementDetails) {
        Paiement updatedPaiement = paiementService.updatePaiement(id, paiementDetails);
        return ResponseEntity.ok(updatedPaiement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable Long id) {
        paiementService.deletePaiement(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<Paiement> getPaiementByReservation(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.getReservationById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvé avec l'id : " + reservationId)); //On gère le cas si la reservation n'existe pas.
        return paiementService.getPaiementByReservation(reservation)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}