package com.example.backend.controllers;

import com.example.backend.dtos.logistique.AgenceRequestDTO;
import com.example.backend.dtos.logistique.AgenceResponseDTO;
import com.example.backend.services.AgenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
public class AgenceController {

    @Autowired
    private AgenceService agenceService;

    @GetMapping("/search")
    public ResponseEntity<List<AgenceResponseDTO>> searchAgences(@RequestParam String query) {
        return ResponseEntity.ok(agenceService.searchAgences(query));
    }

    @PostMapping
    public ResponseEntity<AgenceResponseDTO> createAgence(@RequestBody AgenceRequestDTO dto) {
        AgenceResponseDTO created = agenceService.createAgence(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AgenceResponseDTO>> getAllAgences() {
        return ResponseEntity.ok(agenceService.getAllAgences());
    }

    @GetMapping("/{code}")
    public ResponseEntity<AgenceResponseDTO> getAgenceById(@PathVariable Long code) {
        return ResponseEntity.ok(agenceService.getAgenceById(code));
    }

    @PutMapping("/{code}")
    public ResponseEntity<AgenceResponseDTO> updateAgence(@PathVariable("code") Long code, @RequestBody AgenceRequestDTO dto) {
        return ResponseEntity.ok(agenceService.updateAgence(code, dto));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteAgence(@PathVariable("code") Long code) {
        agenceService.deleteAgence(code);
        return ResponseEntity.noContent().build();
    }
}
