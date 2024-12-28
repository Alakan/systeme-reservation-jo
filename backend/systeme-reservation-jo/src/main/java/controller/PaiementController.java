package controller;

import model.Paiement;
import service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @GetMapping
    public List<Paiement> listerPaiements() {
        return paiementService.listerPaiements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> trouverPaiementParId(@PathVariable Long id) {
        Paiement paiement = paiementService.trouverPaiementParId(id);
        return paiement != null
                ? ResponseEntity.ok(paiement)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Paiement> creerPaiement(@Valid @RequestBody Paiement paiement) {
        Paiement nouveauPaiement = paiementService.creerPaiement(paiement);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauPaiement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> modifierPaiement(@PathVariable Long id, @Valid @RequestBody Paiement paiement) {
        // TODO: Gérer la mise à jour du paiement avec l'ID spécifié
        return paiementService.modifierPaiement(paiement) != null
                ? ResponseEntity.ok(paiement)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPaiement(@PathVariable Long id) {
        paiementService.supprimerPaiement(id);
        return ResponseEntity.noContent().build();
    }
}