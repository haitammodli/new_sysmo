package com.example.backend.models.logistique;

import com.example.backend.enums.StatutExpedition;
import com.example.backend.models.systeme.ReferenceData;
import com.example.backend.models.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expeditions")
public class Expedition {

    @Id
    @Column(name = "numero_expedition", length = 50, nullable = false)
    private String numeroExpedition;

    private Long numerodeclaration;
    @JoinColumn(name = "ramasseur_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User ramasseur;

    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;

    @JoinColumn(name = "nature_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData nature;

    @JoinColumn(name = "type_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData type;

    @JoinColumn(name = "expediteur_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User expediteur;

    @JoinColumn(name = "destinataire_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User destinataire;

    @JoinColumn(name = "agence_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Agence agence;

    @JoinColumn(name = "element_taxation_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ElementTaxation elementTaxation;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatutExpedition statut = StatutExpedition.EN_COURS;
}
