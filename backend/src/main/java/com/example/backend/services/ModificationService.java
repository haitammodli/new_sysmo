package com.example.backend.services;

import com.example.backend.dtos.modification.ModificationRequestDTO;
import com.example.backend.dtos.modification.ModificationResponseDTO;
import com.example.backend.enums.Roles;
import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.operations.Modification;
import com.example.backend.models.users.User;
import com.example.backend.repositories.ExpeditionRepository;
import com.example.backend.repositories.ModificationRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModificationService {

    @Autowired
    private ModificationRepository modificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpeditionRepository expeditionRepository;

    @Autowired
    private EmailService emailService;

   //@ /68480648
    public ModificationResponseDTO soumettreDemande(ModificationRequestDTO dto) {
        Modification mod = new Modification();
        mod.setNumeroExpedition(dto.getNumeroExpedition());
        mod.setTypeModification(dto.getTypeModification());
        mod.setAncienneValeur(dto.getAncienneValeur());
        mod.setNouvelleValeur(dto.getNouvelleValeur());
        mod.setDemandeurId(dto.getDemandeurId());
        mod.setRoleDemandeur(dto.getRoleDemandeur());
        
        mod.setStatut(StatutModificationAnn.EN_ATTENTE);
        mod.setDateDemande(LocalDateTime.now());
        
        Modification savedMod = modificationRepository.save(mod);

        List<Roles> targetRoles = List.of(Roles.AGENTMODIFICATION, Roles.RESPONSABLEMODIFICATION);

        List<User> targetUsers = userRepository.findByRoleIn(targetRoles);

        String[] recipientEmails = targetUsers.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        // 4. SEND THE GROUP ALERT
        for (User recipient : targetUsers) {
            emailService.notifierAgentIndividuel(
                    recipient.getEmail(),
                    savedMod.getNumeroExpedition(),
                    savedMod.getTypeModification().name()
            );
            try {
                // On attend 1.5 seconde entre chaque e-mail pour éviter le blocage Mailtrap
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return mapToResponse(savedMod);
    }

    @Transactional
    public ModificationResponseDTO traiterDemande(Long modificationId, StatutModificationAnn nouveauStatut, Long agentId) {
        Modification mod = modificationRepository.findById(modificationId)
                .orElseThrow(() -> new RuntimeException("Modification non trouvée avec l'ID: " + modificationId));
        
        mod.setStatut(nouveauStatut);
        mod.setAgentModificationId(agentId);
        mod.setDateTraitement(LocalDateTime.now());
        
        // Ensure you use the exact enum value for validated/approved
        if (nouveauStatut == StatutModificationAnn.APPROUVEE) {
            Expedition expedition = expeditionRepository.findById(mod.getNumeroExpedition())
                    .orElseThrow(() -> new RuntimeException("Expédition non trouvée avec l'ID: " + mod.getNumeroExpedition()));
            appliquerModification(expedition, mod);
        }
        
        Modification savedMod = modificationRepository.save(mod);
        userRepository.findById(savedMod.getDemandeurId()).ifPresent(user -> {
            emailService.sendModificationNotification(
                    user.getEmail(),
                    mod.getNumeroExpedition(),
                    nouveauStatut.name()
            );
        });
        return mapToResponse(savedMod);
    }

    private ModificationResponseDTO mapToResponse(Modification mod) {
        return ModificationResponseDTO.builder()
                .id(mod.getId())
                .numeroExpedition(mod.getNumeroExpedition())
                .typeModification(mod.getTypeModification())
                .ancienneValeur(mod.getAncienneValeur())
                .nouvelleValeur(mod.getNouvelleValeur())
                .demandeurId(mod.getDemandeurId())
                .roleDemandeur(mod.getRoleDemandeur())
                .agentModificationId(mod.getAgentModificationId())
                .dateDemande(mod.getDateDemande())
                .dateTraitement(mod.getDateTraitement())
                .statut(mod.getStatut())
                .build();
    }

    /**
     * Applique les modifications validées sur l'entité Expédition.
     */
    private void appliquerModification(Expedition expedition, Modification mod) {
        // Implement logic based on the correct enum values in TypeModification
        switch (mod.getTypeModification()) {
            case ERREUR_POIDS:
                // Parse logic for "poids" and apply to ElementTaxation
                try {
                    double nouveauPoids = Double.parseDouble(mod.getNouvelleValeur());
                    if (expedition.getElementTaxation() != null) {
                        expedition.getElementTaxation().setPoid(nouveauPoids);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valeur de poids invalide: " + mod.getNouvelleValeur());
                }
                break;
                
            case MODIFICATION_VALEUR_DECLARE:
                // Parse logic for the declared value
                try {
                    double nouvelleValeurDecl = Double.parseDouble(mod.getNouvelleValeur());
                    if (expedition.getElementTaxation() != null) {
                        expedition.getElementTaxation().setValeurDeclaree(nouvelleValeurDecl);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valeur déclarée invalide: " + mod.getNouvelleValeur());
                }
                break;
                
            case ERREUR_DESTINATION_CLIENT:
                // Typically you would fetch a ReferenceData for destination, or update user destination.
                // Depending on actual property on Expedition (e.g. expedition.getDestinataire())
                System.out.println("Application de ERREUR_DESTINATION_CLIENT avec nouvelle valeur: " + mod.getNouvelleValeur());
                break;
                
            default:
                // Other cases might require other fields to be mapped or custom logic
                System.out.println("En train de traiter le type de modification sans map automatique: " + mod.getTypeModification());
                break;
        }
        
        expeditionRepository.save(expedition);
    }
}
