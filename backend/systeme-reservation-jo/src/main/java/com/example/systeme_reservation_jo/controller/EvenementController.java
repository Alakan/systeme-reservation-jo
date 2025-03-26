package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.service.EvenementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evenements")
@CrossOrigin(origins = "http://localhost:3000") // À changer une fois le front créé
public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        return ResponseEntity.ok(evenementService.getAllEvenements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Integer id) {
        Optional<Evenement> evenement = evenementService.getEvenementById(id);
        return evenement.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvenement(@Valid @RequestBody Evenement evenement) {
        Evenement createdEvenement = evenementService.createEvenement(evenement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvenement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable Integer id, @Valid @RequestBody Evenement evenement) {
        Optional<Evenement> existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Evenement updatedEvenement = evenementService.updateEvenement(id, evenement);
        return ResponseEntity.ok(updatedEvenement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Integer id) {
        Optional<Evenement> existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/between-dates")
    public ResponseEntity<List<Evenement>> getEvenementsBetweenDates(
            @RequestParam("start") LocalDateTime dateDebut,
            @RequestParam("end") LocalDateTime dateFin) {
        List<Evenement> evenements = evenementService.findEvenementsBetweenDates(dateDebut, dateFin);
        return ResponseEntity.ok(evenements);
    }
}