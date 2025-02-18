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
    private Long id;

    @NotBlank(message = "Le nom de l'événement ne peut pas être vide")
    @Column(nullable = false)
    private String nom;

    private String description;

    @NotNull(message = "La date et l'heure de l'événement ne peuvent pas être nulles")
    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @NotBlank(message = "Le lieu de l'événement ne peut pas être vide")
    @Column(nullable = false)
    private String lieu;

    @NotNull(message = "La capacité totale ne peut pas être nulle")
    @PositiveOrZero(message = "La capacité totale doit être supérieure ou égale à zéro")
    @Column(nullable = false)
    private int capaciteTotale;

    @NotNull(message = "Le nombre de places restantes ne peut pas être nul")
    @Column(nullable = false)
    private int placesRestantes; // Peut être calculé, ou mis à jour via un trigger/une procédure stockée

    private String categorie;

    @NotNull(message = "Le prix unitaire du billet ne doit pas être nul")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(nullable = false)
    private BigDecimal prix;

    //Pas de relation OneToMany avec Billet ici, car un billet est lié à un seul événement.

}