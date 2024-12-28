package service;

import model.Reservation;
import repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation creerReservation(Reservation reservation) {
        // TODO: Ajouter la validation des données, la gestion des erreurs et la logique de réservation
        return reservationRepository.save(reservation);
    }

    public Reservation modifierReservation(Reservation reservation) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return reservationRepository.save(reservation);
    }

    public void supprimerReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation trouverReservationParId(Long id) {
        // TODO: Gérer le cas où la réservation n'est pas trouvée
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> listerReservations() {
        return reservationRepository.findAll();
    }

    // TODO: Ajouter des méthodes pour rechercher des réservations par utilisateur, par statut, etc.
}