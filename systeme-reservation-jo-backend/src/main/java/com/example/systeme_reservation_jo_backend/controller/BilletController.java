package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.model.Billet;
import com.example.systeme_reservation_jo_backend.service.BilletService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/billets")
@CrossOrigin(origins = {"http://localhost:3000", "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"})
@Slf4j
public class BilletController {

    private final BilletService billetService;

    public BilletController(BilletService billetService) {
        this.billetService = billetService;
    }

    @GetMapping
    public List<Billet> getAllBillets() {
        log.info("Récupération de tous les billets");
        return billetService.getAllBillets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billet> getBilletById(@PathVariable Long id) {
        log.info("Récupération du billet avec ID : {}", id);
        return billetService.getBilletById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Nouvel endpoint pour récupérer un billet par l'ID de la réservation
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<Billet> getBilletByReservationId(@PathVariable Long reservationId) {
        log.info("Récupération du billet pour la réservation ID : {}", reservationId);
        return billetService.getBilletByReservationId(reservationId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Billet> createBillet(@Valid @RequestBody Billet billet) { //ResponseEntity<?>
        log.info("Payload reçu pour création de billet : {}", billet);

        if (billetService.existsByNumeroBillet(billet.getNumeroBillet())) {
            log.warn("Échec de création - Le numéro de billet {} existe déjà", billet.getNumeroBillet());
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // .body("Erreur")
        }

        Billet savedBillet = billetService.saveBillet(billet);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBillet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Billet> updateBillet(@PathVariable Long id, @Valid @RequestBody Billet billetDetails) {
        log.info("Mise à jour du billet avec ID : {}. Payload : {}", id, billetDetails);
        Billet updatedBillet = billetService.updateBillet(id, billetDetails);
        return ResponseEntity.ok(updatedBillet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillet(@PathVariable Long id) {
        log.info("Suppression du billet avec ID : {}", id);
        billetService.deleteBillet(id);
        return ResponseEntity.noContent().build();
    }
}
