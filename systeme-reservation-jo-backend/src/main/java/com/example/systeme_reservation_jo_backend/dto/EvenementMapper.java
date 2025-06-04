package com.example.systeme_reservation_jo_backend.dto;

import com.example.systeme_reservation_jo_backend.model.Evenement;

public class EvenementMapper {
    public static EvenementDTO toDTO(Evenement evenement) {
        EvenementDTO dto = new EvenementDTO();
        dto.setId(evenement.getId());
        dto.setTitre(evenement.getTitre());
        dto.setDescription(evenement.getDescription());
        dto.setDateEvenement(evenement.getDateEvenement());
        dto.setLieu(evenement.getLieu());
        dto.setPrix(evenement.getPrix()); // Ajout du prix dans le DTO
        return dto;
    }
}
