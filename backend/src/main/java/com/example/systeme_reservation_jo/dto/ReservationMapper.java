package com.example.systeme_reservation_jo.dto;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;

public class ReservationMapper {
    public static ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDateReservation(reservation.getDateReservation());
        dto.setNombreBillets(reservation.getNombreBillets());
        dto.setStatut(reservation.getStatut().name());
        dto.setModePaiement(reservation.getModePaiement() != null ? reservation.getModePaiement().name() : null);

        Evenement ev = reservation.getEvenement();
        if (ev != null) {
            EvenementDTO evDto = new EvenementDTO();
            evDto.setId(ev.getId());
            evDto.setTitre(ev.getTitre());
            evDto.setDescription(ev.getDescription());
            evDto.setDateEvenement(ev.getDateEvenement());
            evDto.setLieu(ev.getLieu());
            dto.setEvenement(evDto);
        } else {
            // Optionnel : Vous pouvez définir un message ou laisser "null"
            // dto.setEvenement(new EvenementDTO("Événement inconnu", ...));
            // Pour l'instant, si c'est null, votre UI affichera "Événement inconnu"
        }
        return dto;
    }
}
