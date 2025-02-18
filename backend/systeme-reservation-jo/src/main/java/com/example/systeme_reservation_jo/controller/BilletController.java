package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.BilletService;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.example.systeme_reservation_jo.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billets")
public class BilletController {

    @Autowired
    private BilletService billetService;
    @Autowired
    private EvenementService evenementService; // Injecte EvenementService
    @Autowired
    private UtilisateurService utilisateurService;


    @GetMapping
    public List<Billet> getAllBillets() {
        return billetService.getAllBillets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billet> getBilletById(@PathVariable Long id) {
        return billetService.getBilletById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Billet> createBillet(@Valid @RequestBody Billet billet) {
        Billet savedBillet = billetService.saveBillet(billet);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBillet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Billet> updateBillet(@PathVariable Long id, @Valid @RequestBody Billet billetDetails) {
        Billet updatedBillet = billetService.updateBillet(id, billetDetails);
        return ResponseEntity.ok(updatedBillet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillet(@PathVariable Long id) {
        billetService.deleteBillet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<Billet>> getBilletsByEvenement(@PathVariable Long evenementId) {
        Evenement evenement = evenementService.getEvenementById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'id : " + evenementId)); // Gérer le cas où l'événement n'existe pas
        List<Billet> billets = billetService.getBilletsByEvenement(evenement);
        return ResponseEntity.ok(billets);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Billet>> getBilletsByUtilisateur(@PathVariable Long utilisateurId) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + utilisateurId));
        List<Billet> billets = billetService.getBilletsByUtilisateur(utilisateur);
        return ResponseEntity.ok(billets);
    }
    @GetMapping("/statut/{statut}")
    public  ResponseEntity<List<Billet>> getBilletByStatut(@PathVariable String statut){
        List<Billet> billets = billetService.getBilletsByStatut(statut);
        return ResponseEntity.ok(billets);
    }
}