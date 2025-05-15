package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.service.BilletService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les billets.
 */
@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private static final Logger logger = LoggerFactory.getLogger(BilletController.class); // Logger pour suivre les actions

    private final BilletService billetService;

    @Autowired
    public BilletController(BilletService billetService) {
        this.billetService = billetService;
    }

    /**
     * Récupère tous les billets.
     *
     * @return Liste de tous les billets
     */
    @GetMapping
    public List<Billet> getAllBillets() {
        logger.info("Récupération de tous les billets");
        return billetService.getAllBillets();
    }

    /**
     * Récupère un billet spécifique par son ID.
     *
     * @param id ID du billet
     * @return Billet correspondant ou 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Billet> getBilletById(@PathVariable Long id) {
        logger.info("Récupération du billet avec ID : {}", id);
        return billetService.getBilletById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau billet.
     *
     * @param billet Données du billet à créer
     * @return Billet créé avec un statut 201 Created
     */
    @PostMapping
    public ResponseEntity<Billet> createBillet(@Valid @RequestBody Billet billet) {
        logger.info("Création d'un billet avec les données : {}", billet);
        Billet savedBillet = billetService.saveBillet(billet);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBillet);
    }

    /**
     * Met à jour un billet existant.
     *
     * @param id ID du billet à mettre à jour
     * @param billetDetails Nouvelles données du billet
     * @return Billet mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Billet> updateBillet(@PathVariable Long id, @Valid @RequestBody Billet billetDetails) {
        logger.info("Mise à jour du billet avec ID : {}", id);
        Billet updatedBillet = billetService.updateBillet(id, billetDetails);
        return ResponseEntity.ok(updatedBillet);
    }

    /**
     * Supprime un billet spécifique.
     *
     * @param id ID du billet à supprimer
     * @return Réponse vide avec un statut 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillet(@PathVariable Long id) {
        logger.info("Suppression du billet avec ID : {}", id);
        billetService.deleteBillet(id);
        return ResponseEntity.noContent().build();
    }
}
