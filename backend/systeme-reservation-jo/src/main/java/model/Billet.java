package model;

import jakarta.persistence.*;

@Entity
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeBillet type;

    private double prix;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // Constructeurs, getters et setters
}