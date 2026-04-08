package com.example.backend.controllers;

import com.example.backend.dtos.modification.ModificationRequestDTO;
import com.example.backend.dtos.modification.ModificationResponseDTO;
import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.services.ModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<ModificationResponseDTO>> getAll() {
        return ResponseEntity.ok(modificationService.getAllModifications());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(modificationService.getModificationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<ModificationResponseDTO>> search(@RequestParam String critere) {
        return ResponseEntity.ok(modificationService.rechercherMultiple(critere));
    }
}
