package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.TypeBillet; // Importez l'enum TypeBillet
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.BilletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BilletServiceImpl implements BilletService {

    @Autowired
    private BilletRepository billetRepository;

    @Override
    public Billet saveBillet(Billet billet) {
        return billetRepository.save(billet);
    }

    @Override
    public List<Billet> getAllBillets() {
        return billetRepository.findAll();
    }

    @Override
    public Optional<Billet> getBilletById(Integer id) { // Integer ici
        return billetRepository.findById(id);
    }

    @Override
    public Billet updateBillet(Integer id, Billet billetDetails) { // Integer ici
        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billet non trouvé avec l'id : " + id));

        billet.setEvenement(billetDetails.getEvenement());
        billet.setUtilisateur(billetDetails.getUtilisateur());
        billet.setDateReservation(billetDetails.getDateReservation());
        billet.setNumeroBillet(billetDetails.getNumeroBillet());
        billet.setStatut(billetDetails.getStatut());
        billet.setType(billetDetails.getType()); // Assurez-vous que le setter pour type existe
        billet.setReservation(billetDetails.getReservation());

        return billetRepository.save(billet);
    }

    @Override
    public void deleteBillet(Integer id) { // Integer ici
        billetRepository.deleteById(id); // Integer
    }

    @Override
    public List<Billet> getBilletsByEvenement(Evenement evenement) {
        return billetRepository.findByEvenement(evenement);
    }
    @Override
    public List<Billet> getBilletsByUtilisateur(Utilisateur utilisateur){
        return billetRepository.findByUtilisateur(utilisateur);
    }
    @Override
    public List<Billet> getBilletsByStatut(TypeBillet statut){ // Type modifié pour utiliser l'enum
        return  billetRepository.findByStatut(statut);
    }
}