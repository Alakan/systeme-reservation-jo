// src/main/java/com/example/systeme_reservation_jo_backend/dto/PaiementDTO.java
package com.example.systeme_reservation_jo_backend.dto;

import com.example.systeme_reservation_jo_backend.model.ModePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaiementDTO {

    /** Identifiant du paiement (null à la création) */
    private Long id;

    /** Réservation associée (seulement l’id) */
    @NotNull(message = "L'identifiant de la réservation est obligatoire")
    private Long reservationId;

    /** Montant payé, > 0 */
    @NotNull(message = "Le montant ne peut pas être nul")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être > 0")
    private BigDecimal montant;

    /** Date du paiement (sera positionnée si null) */
    private LocalDateTime datePaiement;

    /** Mode de paiement (enum ordinal CARTE, PAYPAL, VIREMENT) */
    @NotNull(message = "La méthode de paiement est obligatoire")
    private ModePaiement methodePaiement;

    /** Statut du paiement */
    @NotBlank(message = "Le statut du paiement est obligatoire")
    private String statut;

    /** Identifiant transactionnel optionnel */
    private String transactionId;
}
