package com.example.systeme_reservation_jo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;


@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La réservation doit être associée à un utilisateur")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @NotNull(message = "La date de réservation ne peut pas être nulle")
    @Column(nullable = false)
    private LocalDateTime dateReservation;

    @NotNull(message = "Le nombre de billets doit être spécifié")
    @Positive(message = "Le nombre de billets doit être supérieur à zéro")
    @Column(nullable = false)
    private int nombreBillets;


    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Billet> billets = new HashSet<>();

    @Column(nullable = false)
    private String statut; // "EN_ATTENTE", "CONFIRMEE", "ANNULEE"

    // Méthodes utilitaires pour gérer la relation bi-directionnelle avec Billet
    public void addBillet(Billet billet) {
        billets.add(billet);
        billet.setReservation(this);
    }

    public void removeBillet(Billet billet) {
        billets.remove(billet);
        billet.setReservation(null);
    }
}