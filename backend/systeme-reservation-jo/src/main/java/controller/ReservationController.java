package controller;

import model.Reservation;
import service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<Reservation> listerReservations() {
        return reservationService.listerReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> trouverReservationParId(@PathVariable Long id) {
        Reservation reservation = reservationService.trouverReservationParId(id);
        return reservation != null
                ? ResponseEntity.ok(reservation)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Reservation> creerReservation(@Valid @RequestBody Reservation reservation) {
        Reservation nouvelleReservation = reservationService.creerReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> modifierReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservation) {
        // TODO: Gérer la mise à jour de la réservation avec l'ID spécifié
        return reservationService.modifierReservation(reservation) != null
                ? ResponseEntity.ok(reservation)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerReservation(@PathVariable Long id) {
        reservationService.supprimerReservation(id);
        return ResponseEntity.noContent().build();
    }
}