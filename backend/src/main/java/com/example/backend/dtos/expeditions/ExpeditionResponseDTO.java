package com.example.backend.dtos.expeditions;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExpeditionResponseDTO {
    // Core Info
    private Long numeroexpedition;
    private Long numerodeclaration;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;
    private String statut;
    
    // Core Relationships & Participants (Names for the UI)
    private Long expiditeurId;
    private String expiditeurNom;
    private Long distinataireId;
    private String distinataireNom;
    private Long ramasseurId;
    private String ramasseurNom;

    private Long agenceId;
    private String agenceNom;

    private Long natureId;
    private Long typeId;

    // ElementTaxation Info
    private int colis;
    private double poid;
    private double volume;
    private int etiquette;
    private double encombrement;
    private double valeurDeclaree;
    private double fond;
    private double ht;
    private double tva;
    private double ttc;
    private boolean bl;
    private String numerobl;
    private boolean facture;
    private String numerofacture;
    private String comment;
    private Long ref_regl;
    private double ps;

    // Taxation Reference relationships
    private Long modeReglId;
    private Long portId;
    private Long catprodId;
    private Long creditId;
    private Long unitId;
    private Long livraisonId;
    private Long taxationId;
}