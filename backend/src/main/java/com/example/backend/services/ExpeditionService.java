package com.example.backend.services;

import com.example.backend.dtos.expeditions.ExpeditionRequestDTO;
import com.example.backend.dtos.expeditions.ExpeditionResponseDTO;
import com.example.backend.enums.StatutExpedition;
import com.example.backend.models.logistique.Agence;
import com.example.backend.models.logistique.ElementTaxation;
import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.systeme.ReferenceData;
import com.example.backend.models.users.User;
import com.example.backend.repositories.AgenceRepository;
import com.example.backend.repositories.ElementTaxationRepository;
import com.example.backend.repositories.ExpeditionRepository;
import com.example.backend.repositories.ReferenceDataRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpeditionService {

    @Autowired
    private ExpeditionRepository expeditionRepository;
    
    @Autowired
    private ElementTaxationRepository elementTaxationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AgenceRepository agenceRepository;
    
    @Autowired
    private ReferenceDataRepository referenceDataRepository;

    @Transactional
    public ExpeditionResponseDTO creerExpedition(ExpeditionRequestDTO dto) {
        
        // 1. Fetch Relationships
        User expiditeur = userRepository.findById(dto.getExpiditeurId())
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé avec ID: " + dto.getExpiditeurId()));
                
        User destinataire = userRepository.findById(dto.getDistinataireId())
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé avec ID: " + dto.getDistinataireId()));
                
        Agence agence = agenceRepository.findById(dto.getAgenceId())
                .orElseThrow(() -> new RuntimeException("Agence non trouvée avec ID: " + dto.getAgenceId()));

        ReferenceData nature = dto.getNatureId() != null ? referenceDataRepository.findById(dto.getNatureId()).orElse(null) : null;
        ReferenceData type = dto.getTypeId() != null ? referenceDataRepository.findById(dto.getTypeId()).orElse(null) : null;

        // 2. Create and Save ElementTaxation
        ElementTaxation taxation = new ElementTaxation();
        taxation.setPoid(dto.getPoid());
        taxation.setVolume(dto.getVolume());
        taxation.setHt(dto.getHt());
        taxation.setTva(dto.getTva());
        taxation.setTtc(dto.getHt() + dto.getTva()); // ttc = ht + tva
        
        ElementTaxation savedTaxation = elementTaxationRepository.save(taxation);

        // 3. Create and Save Expedition
        Expedition expedition = new Expedition();
        expedition.setExpiditeur(expiditeur);
        expedition.setDistinataire(destinataire);
        expedition.setAgence(agence);
        expedition.setNature(nature);
        expedition.setType(type);
        expedition.setElementTaxation(savedTaxation);
        expedition.setDateCreation(LocalDateTime.now());
        expedition.setStatut(StatutExpedition.EN_COURS);

        Expedition savedExpedition = expeditionRepository.save(expedition);

        // 4. Map to DTO
        return mapToResponse(savedExpedition);
    }

    public List<ExpeditionResponseDTO> getAllExpeditions() {
        return expeditionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpeditionResponseDTO mapToResponse(Expedition expedition) {
        ExpeditionResponseDTO res = new ExpeditionResponseDTO();
        res.setNumeroexpedition(expedition.getNumeroexpedition());
        res.setDateCreation(expedition.getDateCreation());
        res.setStatut(expedition.getStatut().name());
        res.setExpiditeurId(expedition.getExpiditeur().getCode());
        res.setDistinataireId(expedition.getDistinataire().getCode());
        res.setAgenceId(expedition.getAgence().getCode());
        
        ElementTaxation taxation = expedition.getElementTaxation();
        if (taxation != null) {
            res.setHt(taxation.getHt());
            res.setTva(taxation.getTva());
            res.setTtc(taxation.getTtc());
        }
        
        return res;
    }
}
