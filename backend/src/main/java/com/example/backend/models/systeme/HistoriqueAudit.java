package com.example.backend.models.systeme;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "historique_audit")
public class HistoriqueAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utilisateur_id", nullable = false, updatable = false)
    private Long utilisateurId;

    @Column(name = "role_utilisateur", nullable = false, updatable = false)
    private String roleUtilisateur;

    @Column(name = "date_action", nullable = false, updatable = false)
    private LocalDateTime dateAction = LocalDateTime.now();

    @Column(length = 50, nullable = false, updatable = false)
    private String typeAction;

    @Column(name = "nom_entite", nullable = false, updatable = false)
    private String nomEntite;

    @Column(name = "entite_id", nullable = false, updatable = false)
    private Long entiteId;

    //DETAILS
    @Column(name = "description_detaillee", columnDefinition = "TEXT", updatable = false)
    private String descriptionDetaillee;

}
