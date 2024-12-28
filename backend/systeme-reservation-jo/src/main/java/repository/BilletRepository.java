package repository;

import model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si besoin,
    // par exemple pour rechercher des billets par événement, par type, etc.
}