package com.example.systeme_reservation_jo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "billets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le billet doit être associé à un événement")
    @ManyToOne(fetch = FetchType.LAZY) // LAZY pour optimiser les performances
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id") // Peut être null si le billet n'est pas encore réservé
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private LocalDateTime dateReservation;

    @Column(unique = true) // Assure l'unicité, mais peut être null si pas encore attribué
    private String numeroBillet;

    @NotBlank(message = "Le statut du billet ne peut pas être vide")
    @Column(nullable = false)
    private String statut; // "RESERVE", "ANNULE", "UTILISE", etc.

}