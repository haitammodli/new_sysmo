package com.example.backend.dtos.modification;

import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.enums.TypeModification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModificationResponseDTO {
    private Long id;
    private String numeroExpedition;
    private TypeModification typeModification;
    private String ancienneValeur;
    private String nouvelleValeur;
    private Long demandeurId;
    private String roleDemandeur;
    private Long agentModificationId;
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
    private StatutModificationAnn statut;
}
