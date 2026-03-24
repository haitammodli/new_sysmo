package com.example.backend.controllers;

import com.example.backend.dtos.ReferenceData.ReferenceDataRequestDTO;
import com.example.backend.dtos.ReferenceData.ReferenceDataResponseDTO;
import com.example.backend.services.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reference-data")
public class ReferenceDataController {

    @Autowired
    private ReferenceDataService referenceDataService;


    @PostMapping
    public ResponseEntity<ReferenceDataResponseDTO> createReferenceData(@RequestBody ReferenceDataRequestDTO dto) {
        ReferenceDataResponseDTO savedDto = referenceDataService.save(dto);
        return ResponseEntity.ok(savedDto);
    }

    // Get all references
    @GetMapping
    public ResponseEntity<List<ReferenceDataResponseDTO>> getAllReferenceData() {
        return ResponseEntity.ok(referenceDataService.getAll());
    }

    // Get a specific reference by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(referenceDataService.getById(id));
        } catch (RuntimeException e) {

            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ReferenceDataResponseDTO>> getActiveByCategory(@PathVariable("category") String category) {
        return ResponseEntity.ok(referenceDataService.getActiveByCategory(category));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleStatus(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(referenceDataService.toggleStatus(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}