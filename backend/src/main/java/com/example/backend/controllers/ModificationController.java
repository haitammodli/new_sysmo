package com.example.backend.controllers;

import com.example.backend.dtos.modification.ModificationRequestDTO;
import com.example.backend.dtos.modification.ModificationResponseDTO;
import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.services.ModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modifications")
public class ModificationController {

    @Autowired
    private ModificationService modificationService;

    @PostMapping("/demander")
    public ResponseEntity<ModificationResponseDTO> soumettreDemande(@RequestBody ModificationRequestDTO dto) {
        ModificationResponseDTO response = modificationService.soumettreDemande(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/traiter")
    public ResponseEntity<ModificationResponseDTO> traiterDemande(
            @PathVariable Long id,
            @RequestParam StatutModificationAnn nouveauStatut,
            @RequestParam Long agentId) {
            
        ModificationResponseDTO response = modificationService.traiterDemande(id, nouveauStatut, agentId);
        return ResponseEntity.ok(response);
    }
}
