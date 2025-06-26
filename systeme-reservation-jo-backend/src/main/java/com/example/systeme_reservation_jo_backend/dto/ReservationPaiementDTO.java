// src/main/java/com/example/systeme_reservation_jo_backend/dto/ReservationPaiementDTO.java
package com.example.systeme_reservation_jo_backend.dto;

import com.example.systeme_reservation_jo_backend.model.ModePaiement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationPaiementDTO {
    @NotNull(message = "La méthode de paiement est obligatoire")
    private ModePaiement methodePaiement;
}
