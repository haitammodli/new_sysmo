package com.example.backend.dtos.modification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class ModificationRequestDTO {

    private Long expeditionId;
    private Long typeModificationId;
    private String ancienneValeur;
    private String nouvelleValeur;
    private Long demandeurId;
}
