package com.example.backend.dtos.modification;

import com.example.backend.enums.TypeModification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModificationRequestDTO {

    private String numeroExpedition;
    private TypeModification typeModification;
    private String ancienneValeur;
    private String nouvelleValeur;
    private Long demandeurId;
    private String roleDemandeur;
}
