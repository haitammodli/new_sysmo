package com.example.backend.models.operations;


import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.enums.TypeAnnulation;
import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.systeme.ReferenceData;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Table(name = "annulations")
@Entity
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Annulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TARGET
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_expedition", nullable = false, unique = true)
    private Expedition expedition;

    private String motifAnnulation;

    @Enumerated(EnumType.STRING)
    @Column(length = 30 ,nullable = false)
    private TypeAnnulation typeAnnulation;

    //DOCUMENTS
    @Column(name = "chemin_fichier_decision", length = 500)
    private String cheminFichierDecision;

    @Column(name = "chemin_souche_jaune", length = 500)
    private String cheminSoucheJaune;

    //ACTORS
    @Column(name = "demandeur_id", nullable = false, updatable = false)
    private Long demandeurId;

    @Column(name = "role_demandeur", nullable = false, updatable = false)
    private String roleDemandeur;

    @Column(name = "agent_modification_id")
    private Long agentModificationId;

    @Column(name = "responsable_validation_id")
    private Long responsableValidationId;

    //TIMESTAMPS & STATUS
    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande = LocalDateTime.now();

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatutModificationAnn statut = StatutModificationAnn.EN_ATTENTE;
}
