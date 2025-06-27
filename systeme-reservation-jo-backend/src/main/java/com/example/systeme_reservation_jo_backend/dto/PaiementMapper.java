// src/main/java/com/example/systeme_reservation_jo_backend/dto/PaiementMapper.java
package com.example.systeme_reservation_jo_backend.dto;

import com.example.systeme_reservation_jo_backend.model.Paiement;
import com.example.systeme_reservation_jo_backend.model.Reservation;

public class PaiementMapper {

    /** Entité → DTO */
    public static PaiementDTO toDTO(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setReservationId(paiement.getReservation().getId());
        dto.setMontant(paiement.getMontant());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setMethodePaiement(paiement.getMethodePaiement());
        dto.setStatut(paiement.getStatut());
        dto.setTransactionId(paiement.getTransactionId());
        return dto;
    }

    /** DTO → Entité (réservation attachée par id, les autres champs sont copiés) */
    public static Paiement fromDTO(PaiementDTO dto) {
        Paiement paiement = new Paiement();
        paiement.setId(dto.getId());
        // Création d'une référence partielle à Reservation (chargée en controller)
        Reservation res = new Reservation();
        res.setId(dto.getReservationId());
        paiement.setReservation(res);

        paiement.setMontant(dto.getMontant());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setMethodePaiement(dto.getMethodePaiement());
        paiement.setStatut(dto.getStatut());
        paiement.setTransactionId(dto.getTransactionId());
        return paiement;
    }
}
