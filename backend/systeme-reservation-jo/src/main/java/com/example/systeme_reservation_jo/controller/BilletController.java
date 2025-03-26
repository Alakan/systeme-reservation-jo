package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.TypeBillet; // Importez l'enum TypeBillet
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
import java.util.Optional;

@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private final BilletService billetService;
    private final EvenementService evenementService;
    private final UtilisateurService utilisateurService;

    @Autowired
    public BilletController(BilletService billetService, EvenementService evenementService, UtilisateurService utilisateurService) {
        this.billetService = billetService;
        this.evenementService = evenementService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Billet> getAllBillets() {
        return billetService.getAllBillets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billet> getBilletById(@PathVariable Integer id) { // Integer ici
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
    public ResponseEntity<Billet> updateBillet(@PathVariable Integer id, @Valid @RequestBody Billet billetDetails) { // Integer ici
        Billet updatedBillet = billetService.updateBillet(id, billetDetails); // Integer
        return ResponseEntity.ok(updatedBillet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillet(@PathVariable Integer id) { // Integer ici
        billetService.deleteBillet(id); // Integer
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<Billet>> getBilletsByEvenement(@PathVariable Integer evenementId) { // Integer ici
        Optional<Evenement> evenementOptional = evenementService.getEvenementById(evenementId); // Integer
        return evenementOptional.map(evenement -> ResponseEntity.ok(billetService.getBilletsByEvenement(evenement)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Billet>> getBilletsByUtilisateur(@PathVariable Integer utilisateurId) { // Integer ici (si Utilisateur utilise Integer pour ID)
        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurById(utilisateurId);// Integer
        if(utilisateurOptional.isPresent()){
            Utilisateur utilisateur = utilisateurOptional.get();
            List<Billet> billets = billetService.getBilletsByUtilisateur(utilisateur);
            return ResponseEntity.ok(billets);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Billet>> getBilletByStatut(@PathVariable String statut) {
        try {
            TypeBillet typeBillet = TypeBillet.valueOf(statut.toUpperCase()); // Convertit la String en enum (insensible à la casse)
            List<Billet> billets = billetService.getBilletsByStatut(typeBillet);
            return ResponseEntity.ok(billets);
        } catch (IllegalArgumentException e) {
            // Gérer le cas où la String ne correspond pas à une valeur de l'enum
            return ResponseEntity.badRequest().body(null); // Ou un message d'erreur plus spécifique
        }
    }
}