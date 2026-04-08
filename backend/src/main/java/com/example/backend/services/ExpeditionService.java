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
        
        // 1. Fetch Relationships optimally
        User expiditeur = dto.getExpiditeurId() != null ? userRepository.getReferenceById(dto.getExpiditeurId()) : null;
        User destinataire = dto.getDistinataireId() != null ? userRepository.getReferenceById(dto.getDistinataireId()) : null;
        User ramasseur = dto.getRamasseurId() != null ? userRepository.getReferenceById(dto.getRamasseurId()) : null;
        Agence agence = dto.getAgenceId() != null ? agenceRepository.getReferenceById(dto.getAgenceId()) : null;

        ReferenceData nature = dto.getNatureId() != null ? referenceDataRepository.getReferenceById(dto.getNatureId()) : null;
        ReferenceData type = dto.getTypeId() != null ? referenceDataRepository.getReferenceById(dto.getTypeId()) : null;
        ReferenceData modeRegl = dto.getModeReglId() != null ? referenceDataRepository.getReferenceById(dto.getModeReglId()) : null;
        ReferenceData port = dto.getPortId() != null ? referenceDataRepository.getReferenceById(dto.getPortId()) : null;
        ReferenceData catprod = dto.getCatprodId() != null ? referenceDataRepository.getReferenceById(dto.getCatprodId()) : null;
        ReferenceData credit = dto.getCreditId() != null ? referenceDataRepository.getReferenceById(dto.getCreditId()) : null;
        ReferenceData unit = dto.getUnitId() != null ? referenceDataRepository.getReferenceById(dto.getUnitId()) : null;
        ReferenceData livraison = dto.getLivraisonId() != null ? referenceDataRepository.getReferenceById(dto.getLivraisonId()) : null;
        ReferenceData taxationRef = dto.getTaxationId() != null ? referenceDataRepository.getReferenceById(dto.getTaxationId()) : null;

        // 2. Create and Save ElementTaxation
        ElementTaxation taxation = new ElementTaxation();
        taxation.setColis(dto.getColis());
        taxation.setPoid(dto.getPoid());
        taxation.setVolume(dto.getVolume());
        taxation.setEtiquette(dto.getEtiquette());
        taxation.setEncombrement(dto.getEncombrement());
        taxation.setValeurDeclaree(dto.getValeurDeclaree());
        taxation.setFond(dto.getFond());
        taxation.setHt(dto.getHt());
        taxation.setTva(dto.getTva());
        
        // Calculate TTC
        taxation.setTtc(dto.getHt() + (dto.getHt() * dto.getTva() / 100));
        
        taxation.setBl(dto.isBl() ? 1 : 0);
        taxation.setNumerobl(dto.getNumerobl());
        taxation.setFacture(dto.isFacture() ? 1 : 0);
        taxation.setNumerofacture(dto.getNumerofacture());
        taxation.setComment(dto.getComment());
        taxation.setRef_regl(dto.getRef_regl());
        taxation.setPs(dto.getPs());

        taxation.setMode_regl(modeRegl);
        taxation.setPort(port);
        taxation.setCatprod(catprod);
        taxation.setCredit(credit);
        taxation.setUnit(unit);
        taxation.setLivraison(livraison);
        taxation.setTaxation(taxationRef);

        ElementTaxation savedTaxation = elementTaxationRepository.save(taxation);

        // 3. Create and Save Expedition
        Expedition expedition = new Expedition();
        expedition.setExpediteur(expiditeur);
        expedition.setDestinataire(destinataire);
        expedition.setAgence(agence);
        expedition.setNature(nature);
        expedition.setType(type);
        expedition.setElementTaxation(savedTaxation);
        expedition.setDateCreation(LocalDateTime.now());
        expedition.setNumeroExpedition(dto.getNumeroExpedition());
        expedition.setNumerodeclaration(dto.getNumerodeclaration());
        expedition.setRamasseur(ramasseur);
        expedition.setDateLivraison(dto.getDateLivraison());
        expedition.setAdresseLivraison(dto.getAdresseLivraison());
        expedition.setStatut(StatutExpedition.EN_COURS);

        Expedition savedExpedition = expeditionRepository.save(expedition);

        return mapToResponse(savedExpedition);
    }

    public List<ExpeditionResponseDTO> getAllExpeditions() {
        return expeditionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExpeditionResponseDTO getExpeditionById(String id) {
        Expedition expedition = expeditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expédition non trouvée avec ID: " + id));
        return mapToResponse(expedition);
    }

    private ExpeditionResponseDTO mapToResponse(Expedition expedition) {
        ExpeditionResponseDTO res = new ExpeditionResponseDTO();
        res.setNumeroexpedition(expedition.getNumeroExpedition());
        res.setDateCreation(expedition.getDateCreation());
        res.setAdresseLivraison(expedition.getAdresseLivraison());
        res.setStatut(expedition.getStatut().name());
        
        if (expedition.getExpediteur() != null) {
            Long code = expedition.getExpediteur().getCode();
            res.setExpiditeurId(code);
            res.setExpiditeurNom(expedition.getExpediteur().getNom());
        }
        if (expedition.getDestinataire() != null) {
            Long code = expedition.getDestinataire().getCode();
            res.setDistinataireId(code);
            res.setDistinataireNom(expedition.getDestinataire().getNom());
        }
        if (expedition.getRamasseur() != null) {
            Long code = expedition.getRamasseur().getCode();
            res.setRamasseurId(code);
            res.setRamasseurNom(expedition.getRamasseur().getNom());
        }
        if (expedition.getAgence() != null) {
            Long code = expedition.getAgence().getCode();
            res.setAgenceId(code);
            res.setAgenceNom(expedition.getAgence().getNom());
        }
        if (expedition.getNature() != null) {
            res.setNatureId(expedition.getNature().getId());
        }
        if (expedition.getType() != null) {
            res.setTypeId(expedition.getType().getId());
        }

        ElementTaxation taxation = expedition.getElementTaxation();
        if (taxation != null) {
            res.setColis(taxation.getColis());
            res.setPoid(taxation.getPoid());
            res.setVolume(taxation.getVolume());
            res.setEtiquette(taxation.getEtiquette());
            res.setEncombrement(taxation.getEncombrement());
            res.setValeurDeclaree(taxation.getValeurDeclaree());
            res.setFond(taxation.getFond());
            res.setHt(taxation.getHt());
            res.setTva(taxation.getTva());
            res.setTtc(taxation.getTtc());
            res.setBl(taxation.getBl() != 0);
            res.setNumerobl(taxation.getNumerobl());
            res.setFacture(taxation.getFacture() != 0);
            res.setNumerofacture(taxation.getNumerofacture());
            res.setComment(taxation.getComment());
            res.setRef_regl(taxation.getRef_regl());
            res.setPs(taxation.getPs());

            if (taxation.getMode_regl() != null) res.setModeReglId(taxation.getMode_regl().getId());
            if (taxation.getPort() != null) res.setPortId(taxation.getPort().getId());
            if (taxation.getCatprod() != null) res.setCatprodId(taxation.getCatprod().getId());
            if (taxation.getCredit() != null) res.setCreditId(taxation.getCredit().getId());
            if (taxation.getUnit() != null) res.setUnitId(taxation.getUnit().getId());
            if (taxation.getLivraison() != null) res.setLivraisonId(taxation.getLivraison().getId());
            if (taxation.getTaxation() != null) res.setTaxationId(taxation.getTaxation().getId());
        }
        
        return res;
    }
}