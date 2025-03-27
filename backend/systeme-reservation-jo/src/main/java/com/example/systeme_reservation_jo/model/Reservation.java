package com.example.systeme_reservation_jo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @NotNull(message = "L'ID de l'événement ne peut pas être nul")
    @Column(nullable = false)
    private Integer evenementId; // Ajout de l'ID de l'événement

    @NotNull(message = "La date de réservation ne peut pas être nulle")
    @Column(nullable = false)
    private LocalDateTime dateReservation;

    @NotNull(message = "Le nombre de billets doit être spécifié")
    @Positive(message = "Le nombre de billets doit être supérieur à zéro")
    @Column(nullable = false)
    private int nombreBillets;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Billet> billets = new HashSet<>();

    @NotNull(message = "Le statut de la réservation ne peut pas être nul")
    @Column(nullable = false)
    private StatutReservation statut;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Paiement paiement;

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