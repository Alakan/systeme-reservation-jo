// src/main/java/com/example/systeme_reservation_jo_backend/controller/PaiementController.java
package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.dto.PaiementDTO;
import com.example.systeme_reservation_jo_backend.dto.PaiementMapper;
import com.example.systeme_reservation_jo_backend.model.Reservation;
import com.example.systeme_reservation_jo_backend.service.PaiementService;
import com.example.systeme_reservation_jo_backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
})
public class PaiementController {

    private final PaiementService   paiementService;
    private final ReservationService reservationService;

    @Autowired
    public PaiementController(PaiementService paiementService,
                              ReservationService reservationService) {
        this.paiementService = paiementService;
        this.reservationService = reservationService;
    }

    /** Retourne tous les paiements en DTO */
    @GetMapping
    public List<PaiementDTO> getAllPaiements() {
        return paiementService
                .getAllPaiements()
                .stream()
                .map(PaiementMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Retourne un paiement par son id, ou 404 */
    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> getPaiementById(@PathVariable Long id) {
        return paiementService.getPaiementById(id)
                .map(PaiementMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crée un paiement à partir d'un PaiementDTO.
     * Le DTO doit contenir l'id de la réservation associée.
     */
    @PostMapping
    public ResponseEntity<PaiementDTO> createPaiement(
            @Valid @RequestBody PaiementDTO dto) {
        if (dto.getReservationId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Charger la réservation liée
        Reservation res = reservationService
                .getReservationById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException(
                        "Réservation non trouvée avec l'id : " + dto.getReservationId()
                ));

        // Construire l'entité Paiement et l'associer
        var paiement = PaiementMapper.fromDTO(dto);
        paiement.setReservation(res);

        var saved = paiementService.savePaiement(paiement);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaiementMapper.toDTO(saved));
    }

    /**
     * Met à jour un paiement existant.
     * On utilise le DTO pour passer les nouveaux champs.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaiementDTO> updatePaiement(
            @PathVariable Long id,
            @Valid @RequestBody PaiementDTO dto) {

        // On ne change pas la réservation: dto.getReservationId() doit rester identique
        var paiementToUpdate = PaiementMapper.fromDTO(dto);
        var updated = paiementService.updatePaiement(id, paiementToUpdate);
        return ResponseEntity.ok(PaiementMapper.toDTO(updated));
    }

    /** Supprime définitivement un paiement */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable Long id) {
        try {
            paiementService.deletePaiement(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
