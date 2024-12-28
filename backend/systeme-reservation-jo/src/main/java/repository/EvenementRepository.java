package repository;

import model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si besoin,
    // par exemple pour rechercher des événements par nom, par date, etc.
}