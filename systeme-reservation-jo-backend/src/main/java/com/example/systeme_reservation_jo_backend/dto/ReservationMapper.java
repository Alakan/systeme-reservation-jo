// src/main/java/com/example/systeme_reservation_jo_backend/dto/ReservationMapper.java
package com.example.systeme_reservation_jo_backend.dto;

import com.example.systeme_reservation_jo_backend.model.Evenement;
import com.example.systeme_reservation_jo_backend.model.ModePaiement;
import com.example.systeme_reservation_jo_backend.model.Reservation;
import com.example.systeme_reservation_jo_backend.model.StatutReservation;

import java.math.BigDecimal;

public class ReservationMapper {

    public static ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDateReservation(reservation.getDateReservation());
        dto.setNombreBillets(reservation.getNombreBillets());
        dto.setStatut(reservation.getStatut() != null
                ? reservation.getStatut().name()
                : null);
        dto.setModePaiement(reservation.getModePaiement() != null
                ? reservation.getModePaiement().name()
                : null);

        if (reservation.getEvenement() != null) {
            Evenement ev = reservation.getEvenement();
            EvenementDTO evDto = new EvenementDTO();
            evDto.setId(ev.getId());
            evDto.setTitre(ev.getTitre());
            evDto.setDescription(ev.getDescription());
            evDto.setDateEvenement(ev.getDateEvenement());
            evDto.setLieu(ev.getLieu());
            evDto.setPrix(ev.getPrix());
            dto.setEvenement(evDto);

            dto.setPrixUnitaire(ev.getPrix());
            dto.setPrixTotal(
                    ev.getPrix().multiply(BigDecimal.valueOf(reservation.getNombreBillets()))
            );
        }
        return dto;
    }

    /**
     * Construit une entité Reservation à partir du DTO.
     * ATTENTION : ne set pas ici l'utilisateur ni le statut/modePaiement complets,
     * ils sont gérés en controller/service.
     */
    public static Reservation fromDTO(ReservationDTO dto) {
        Reservation r = new Reservation();
        r.setId(dto.getId());
        r.setNombreBillets(dto.getNombreBillets());
        r.setDateReservation(dto.getDateReservation());

        // On ne récupère que l'id de l'événement, le controller se charge de charger l'entité complète
        if (dto.getEvenement() != null && dto.getEvenement().getId() != null) {
            Evenement ev = new Evenement();
            ev.setId(dto.getEvenement().getId());
            r.setEvenement(ev);
        }

        // Pour l'update tu pourras aussi parser statut / modePaiement si besoin :
        if (dto.getStatut() != null) {
            r.setStatut(StatutReservation.valueOf(dto.getStatut()));
        }
        if (dto.getModePaiement() != null) {
            r.setModePaiement(ModePaiement.valueOf(dto.getModePaiement()));
        }
        return r;
    }
}
