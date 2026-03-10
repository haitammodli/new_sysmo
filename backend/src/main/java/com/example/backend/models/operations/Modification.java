package com.example.backend.models.operations;

import com.example.backend.enums.Statut;
import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.systeme.ReferenceData;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "modifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Modification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TARGET
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_expedition", nullable = false)
    private Expedition expedition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_modification_id", nullable = false)
    private ReferenceData typeModification;

    //THE CHANGES
    @Column(length = 255)
    private String ancienneValeur;

    @Column(length = 255)
    private String nouvelleValeur;

    //DOCUMENTS
    @Column(name = "chemin_fichier_decision", length = 500)
    private String cheminFichierDecision;

    //ACTORS
    @Column(name = "demandeur_id", nullable = false, updatable = false)
    private Long demandeurId;

    @Column(name = "role_demandeur", nullable = false, updatable = false)
    private String roleDemandeur; // "CLIENT" or "CHEF_AGENCE"

    @Column(name = "agent_modification_id")
    private Long agentModificationId; // The Agent who validates/rejects the request

    //TIMESTAMPS & STATUS
    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande = LocalDateTime.now();

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "responsable_validation_id")
    private Long responsableValidationId;

    @Column(name = "email_justification_recu", nullable = false)
    private boolean emailJustificationRecu = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Statut statut = Statut.EN_ATTENTE;
}