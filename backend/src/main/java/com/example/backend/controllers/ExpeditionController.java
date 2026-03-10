package com.example.backend.controllers;

import com.example.backend.dtos.expeditions.ExpeditionRequestDTO;
import com.example.backend.dtos.expeditions.ExpeditionResponseDTO;
import com.example.backend.services.ExpeditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expeditions")
public class ExpeditionController {

    @Autowired
    private ExpeditionService expeditionService;

    @PostMapping
    public ResponseEntity<ExpeditionResponseDTO> createExpedition(@RequestBody ExpeditionRequestDTO requestDTO) {
        ExpeditionResponseDTO response = expeditionService.creerExpedition(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExpeditionResponseDTO>> getAllExpeditions() {
        List<ExpeditionResponseDTO> response = expeditionService.getAllExpeditions();
        return ResponseEntity.ok(response);
    }
}
