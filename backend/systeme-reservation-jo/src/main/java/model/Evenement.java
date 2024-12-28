package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private LocalDate date;

    private LocalTime heure;

    private String lieu;

    private String description;

    private int capacite;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    private List<Billet> billets;

    // Constructeurs, getters et setters
}