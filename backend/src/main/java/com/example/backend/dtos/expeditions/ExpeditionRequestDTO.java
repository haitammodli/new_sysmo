package com.example.backend.dtos.expeditions;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExpeditionRequestDTO {


    private String numeroExpedition;
    private Long numerodeclaration;
    private Long ramasseurId;
    private LocalDateTime dateLivraison;
    private String adresseLivraison;

    // Relationships
    private Long expiditeurId;
    private Long distinataireId;
    private Long agenceId;
    private Long natureId;
    private Long typeId;

    // ElementTaxation Details
    private int colis;
    private double poid;
    private double volume;
    private int etiquette;
    private double encombrement;
    private double valeurDeclaree;
    private double fond;
    private double ht;
    private double tva;
    
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