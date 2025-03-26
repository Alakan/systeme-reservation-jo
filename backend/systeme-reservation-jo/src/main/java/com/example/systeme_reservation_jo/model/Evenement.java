package com.example.systeme_reservation_jo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "evenements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Le titre de l'événement ne peut pas être vide") // Message corrigé
    private String titre;

    private String description;

    @NotNull(message = "La date et l'heure de l'événement ne peuvent pas être nulles")
    @Column(nullable = false)
    private LocalDateTime dateEvenement; // On garde uniquement ce champ

    @NotBlank(message = "Le lieu de l'événement ne peut pas être vide")
    private String lieu;

    @NotNull(message = "La capacité totale ne peut pas être nulle")
    @PositiveOrZero(message = "La capacité totale doit être supérieure ou égale à zéro")
    @Column(nullable = false)
    private int capaciteTotale;

    //On garde pour le moment, mais il faudra le gérer correctement plus tard.
    @NotNull(message = "Le nombre de places restantes ne peut pas être nul")
    @Column(nullable = false)
    private int placesRestantes;

    private String categorie;

    @NotNull(message = "Le prix unitaire du billet ne doit pas être nul")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(nullable = false)
    private BigDecimal prix;
}

