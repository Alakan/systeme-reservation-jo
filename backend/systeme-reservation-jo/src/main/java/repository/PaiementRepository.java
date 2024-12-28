package repository;

import model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si besoin,
    // par exemple pour rechercher des paiements par mode de paiement, par date, etc.
}