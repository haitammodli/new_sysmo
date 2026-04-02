package com.example.backend.services;

import com.example.backend.dtos.logistique.AgenceRequestDTO;
import com.example.backend.dtos.logistique.AgenceResponseDTO;
import com.example.backend.models.logistique.Agence;
import com.example.backend.models.users.ChefAgence;
import com.example.backend.models.users.User;
import com.example.backend.repositories.AgenceRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgenceService {

    @Autowired
    private AgenceRepository agenceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AgenceResponseDTO> getAllAgences() {
        return agenceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AgenceResponseDTO> searchAgences(String query) {
        return agenceRepository.findByNomContainingIgnoreCaseOrVilleContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AgenceResponseDTO getAgenceById(Long code) {
        Agence agence = agenceRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Agence non trouvée : " + code));
        return mapToResponse(agence);
    }

    @Transactional
    public AgenceResponseDTO createAgence(AgenceRequestDTO dto) {
        Agence agence = new Agence();
        agence.setNom(dto.getNom());
        agence.setVille(dto.getVille());

        if (dto.getMatricule() != null) {
            User chef = userRepository.findById(dto.getMatricule())
                    .orElseThrow(() -> new RuntimeException("Chef non trouvé"));

            // 🛠️ FIX: Assign the actual chef we found, not a blank new one
            if (chef instanceof ChefAgence) {
                agence.setChef((ChefAgence) chef);
            } else {
                throw new RuntimeException("L'utilisateur n'est pas un Chef d'Agence");
            }
        }

        return mapToResponse(agenceRepository.save(agence));
    }

    @Transactional
    public AgenceResponseDTO updateAgence(Long code, AgenceRequestDTO dto) {
        Agence agence = agenceRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Agence non trouvée : " + code));

        agence.setNom(dto.getNom());
        agence.setVille(dto.getVille());

        if (dto.getMatricule() != null) {
            User chef = userRepository.findById(dto.getMatricule())
                    .orElseThrow(() -> new RuntimeException("Chef non trouvé"));

            if (chef instanceof ChefAgence) {
                agence.setChef((ChefAgence) chef);
            } else {
                throw new RuntimeException("L'utilisateur n'est pas un Chef d'Agence");
            }
        } else {
            agence.setChef(null);
        }

        return mapToResponse(agenceRepository.save(agence));
    }

    @Transactional
    public void deleteAgence(Long code) {
        if (!agenceRepository.existsById(code)) {
            throw new RuntimeException("Agence non trouvée : " + code);
        }
        agenceRepository.deleteById(code);
    }

    // Mapper
    private AgenceResponseDTO mapToResponse(Agence agence) {
        AgenceResponseDTO res = new AgenceResponseDTO();
        res.setCode(agence.getCode());
        res.setNom(agence.getNom());
        res.setVille(agence.getVille());

        if (agence.getChef() != null) {
            res.setChefMatricule(agence.getChef().getCode());
            res.setChefNomComplet(agence.getChef().getNom() + " " + agence.getChef().getPrenom());
        }
        return res;
    }
}