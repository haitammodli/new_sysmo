package com.example.backend.models.logistique;

import com.example.backend.models.systeme.ReferenceData;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "element_de_taxation")
public class ElementTaxation {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

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
    private int bl;
    private String numerobl;
    private int facture;
    private String numerofacture;
    private String comment;
    private Long ref_regl;

    @JoinColumn(name = "mode_regl")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData mode_regl;
    private double ps;

    @JoinColumn(name = "port_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData port;

    @JoinColumn(name = "categorie_prod_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData catprod;   //categorie de produit

    @JoinColumn(name = "credit_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData credit;

    @JoinColumn(name = "unit_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData unit;

    @JoinColumn(name = "livraison_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData livraison;

    @JoinColumn(name = "taxation_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceData taxation;

}
