package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Billet> billets;

    private LocalDateTime dateReservation;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;

    // Constructeurs, getters et setters
}