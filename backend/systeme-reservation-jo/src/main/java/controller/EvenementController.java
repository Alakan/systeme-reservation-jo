package controller;

import model.Evenement;
import service.EvenementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evenements")
public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    @GetMapping
    public List<Evenement> listerEvenements() {
        return evenementService.listerEvenements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> trouverEvenementParId(@PathVariable Long id) {
        Evenement evenement = evenementService.trouverEvenementParId(id);
        return evenement != null
                ? ResponseEntity.ok(evenement)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Evenement> creerEvenement(@Valid @RequestBody Evenement evenement) {
        Evenement nouvelEvenement = evenementService.creerEvenement(evenement);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelEvenement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> modifierEvenement(@PathVariable Long id, @Valid @RequestBody Evenement evenement) {
        // TODO: Gérer la mise à jour de l'événement avec l'ID spécifié
        return evenementService.modifierEvenement(evenement) != null
                ? ResponseEntity.ok(evenement)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEvenement(@PathVariable Long id) {
        evenementService.supprimerEvenement(id);
        return ResponseEntity.noContent().build();
    }
}