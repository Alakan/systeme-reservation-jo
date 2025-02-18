package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.service.EvenementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;


@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    @GetMapping
    public List<Evenement> getAllEvenements() {
        return evenementService.getAllEvenements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        return evenementService.getEvenementById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvenement(@Valid @RequestBody Evenement evenement) {
        Evenement savedEvenement = evenementService.saveEvenement(evenement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvenement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable Long id, @Valid @RequestBody Evenement evenementDetails) {
        Evenement updatedEvenement = evenementService.updateEvenement(id, evenementDetails);
        return ResponseEntity.ok(updatedEvenement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<Evenement>> findEvenementsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Evenement> evenements = evenementService.findEvenementsBetweenDates(start, end);
        return ResponseEntity.ok(evenements);
    }
}