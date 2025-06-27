package com.example.systeme_reservation_jo_backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {

    /** Id uniquement utilisé lors de la modification, null en création */
    private Long id;

    @NotBlank(message = "Le titre est requis")
    private String titre;

    @NotBlank(message = "La description est requise")
    private String description;

    @NotNull(message = "La date et l'heure sont requises")
    private LocalDateTime dateEvenement;

    @NotBlank(message = "Le lieu est requis")
    private String lieu;

    @NotNull(message = "Le prix est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prix;

    @NotNull(message = "La capacité totale est requise")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    private Integer capaciteTotale;

    /** Optionnel : catégorie, si tu souhaites gérer ce champ en back-office */
    // private String categorie;
}
