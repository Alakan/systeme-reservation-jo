package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.model.Evenement;
import com.example.systeme_reservation_jo_backend.service.EvenementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evenements")
@CrossOrigin(origins = {"http://localhost:3000", "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"})
public class EvenementController {

    private static final Logger logger = LoggerFactory.getLogger(EvenementController.class);
    private final EvenementService evenementService;

    @Autowired
    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    /**
     * Récupération de tous les événements publics (seulement ceux actifs).
     * GET /api/evenements
     */
    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenementsPublic();
        return ResponseEntity.ok(evenements);
    }

    /**
     * Récupération d'un événement par son id (seulement s'il est actif).
     * GET /api/evenements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        Optional<Evenement> evenementOpt = evenementService.getEvenementById(id);
        if (evenementOpt.isPresent()) {
            Evenement evenement = evenementOpt.get();
            if (!evenement.isActif()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(evenement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Création d'un événement.
     * (Cette opération peut être réservée aux administrateurs.)
     * POST /api/evenements
     */
    @PostMapping
    public ResponseEntity<Evenement> createEvenement(@Valid @RequestBody Evenement evenement) {
        logger.info("Création de l'événement avec les données : {}", evenement);
        Evenement createdEvenement = evenementService.createEvenement(evenement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvenement);
    }

    /**
     * Mise à jour d'un événement existant.
     * (Opération réservée aux administrateurs.)
     * PUT /api/evenements/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable Long id, @Valid @RequestBody Evenement evenement) {
        Optional<Evenement> existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Evenement updatedEvenement = evenementService.updateEvenement(id, evenement);
        return ResponseEntity.ok(updatedEvenement);
    }

    /**
     * Récupération d'événements entre deux dates.
     * Seuls les événements actifs sont retournés.
     * GET /api/evenements/between-dates?start=YYYY-MM-DDTHH:MM:SS&end=YYYY-MM-DDTHH:MM:SS
     */
    @GetMapping("/between-dates")
    public ResponseEntity<List<Evenement>> getEvenementsBetweenDates(
            @RequestParam("start") LocalDateTime dateDebut,
            @RequestParam("end") LocalDateTime dateFin) {
        List<Evenement> evenements = evenementService.findEvenementsBetweenDates(dateDebut, dateFin);
        evenements.removeIf(e -> !e.isActif());
        return ResponseEntity.ok(evenements);
    }
}
