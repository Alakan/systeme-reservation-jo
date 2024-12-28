package repository;

import model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si besoin,
    // par exemple pour rechercher des réservations par utilisateur, par statut, etc.
}