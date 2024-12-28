package controller;

import model.Billet;
import service.BilletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billets")
public class BilletController {

    @Autowired
    private BilletService billetService;

    @GetMapping
    public List<Billet> listerBillets() {
        return billetService.listerBillets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billet> trouverBilletParId(@PathVariable Long id) {
        Billet billet = billetService.trouverBilletParId(id);
        return billet != null
                ? ResponseEntity.ok(billet)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Billet> creerBillet(@Valid @RequestBody Billet billet) {
        Billet nouveauBillet = billetService.creerBillet(billet);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauBillet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Billet> modifierBillet(@PathVariable Long id, @Valid @RequestBody Billet billet) {
        // TODO: Gérer la mise à jour du billet avec l'ID spécifié
        return billetService.modifierBillet(billet) != null
                ? ResponseEntity.ok(billet)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerBillet(@PathVariable Long id) {
        billetService.supprimerBillet(id);
        return ResponseEntity.noContent().build();
    }
}