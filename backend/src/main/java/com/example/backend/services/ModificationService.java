package com.example.backend.services;

import com.example.backend.dtos.modification.ModificationRequestDTO;
import com.example.backend.dtos.modification.ModificationResponseDTO;
import com.example.backend.enums.Roles;
import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.models.logistique.ElementTaxation;
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

    public ModificationResponseDTO getModificationById(Long id) {
        Modification mod = modificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modification non trouvée avec l'ID: " + id));
        return mapToResponse(mod);
    }
    public List<ModificationResponseDTO> getAllModifications() {
        return modificationRepository.findAll().stream()
                .map(this::mapToResponse) // Réutilise votre excellente méthode de mapping !
                .toList();
    }
    public List<ModificationResponseDTO> rechercherMultiple(String critere) {

        List<String> numerosRecherches = new java.util.ArrayList<>();
        numerosRecherches.add(critere);

        try {
            Long numDeclaration = Long.parseLong(critere);

            expeditionRepository.findByNumerodeclaration(numDeclaration).ifPresent(exp -> {
                numerosRecherches.add(exp.getNumeroExpedition());
            });

        } catch (NumberFormatException e) {
        }

        return modificationRepository.findByNumeroExpeditionIn(numerosRecherches).stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Transactional
    public ModificationResponseDTO soumettreDemande(ModificationRequestDTO dto) {

        // Maximum 2 modifications par expédition
        List<Modification> existantes = modificationRepository.findByNumeroExpedition(dto.getNumeroExpedition());
        if (existantes.size() >= 2) {
            throw new RuntimeException("Limite atteinte : Un maximum de deux modifications par expédition est autorisé.");
        }

        //Interdit de modifier une expédition facturée
        Expedition exp = expeditionRepository.findById(dto.getNumeroExpedition())
                .orElseThrow(() -> new RuntimeException("Expédition non trouvée avec l'ID: " + dto.getNumeroExpedition()));

        if (exp.getElementTaxation() != null && exp.getElementTaxation().getNumerofacture() != null) {
            throw new RuntimeException("Interdit de modifier une expédition déjà facturée !");
        }
        // ------------------------------

        //CREATION DE LA MODIFICATION ---
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

        //NOTIFICATIONS EMAILS ---
        List<Roles> targetRoles = List.of(Roles.AGENTMODIFICATION, Roles.RESPONSABLEMODIFICATION);
        List<User> targetUsers = userRepository.findByRoleIn(targetRoles);

        for (User recipient : targetUsers) {
            emailService.notifierAgentIndividuel(
                    recipient.getEmail(),
                    savedMod.getNumeroExpedition(),
                    savedMod.getTypeModification().name()
            );
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
    /**
     * Applique les modifications validées sur l'entité Expédition et son ElementTaxation.
     */
    private void appliquerModification(Expedition expedition, Modification mod) {

        // Check if the Expedition has an ElementTaxation, if not, we can't update financial/weight data
        ElementTaxation taxation = expedition.getElementTaxation();

        switch (mod.getTypeModification()) {

            // --- 1. MODIFICATIONS DE POIDS / VOLUME ---
            case ERREUR_POIDS:
                try {
                    double nouveauPoids = Double.parseDouble(mod.getNouvelleValeur());
                    if (taxation != null) {
                        taxation.setPoid(nouveauPoids);
                    } else {
                        System.out.println("Erreur: ElementTaxation manquant pour l'expédition " + expedition.getNumeroExpedition());
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valeur de poids invalide: " + mod.getNouvelleValeur());
                }
                break;

            // --- 2. MODIFICATIONS FINANCIÈRES (Prix / Montant) ---
            case ERREUR_TAXATION:
            case ERREUR_CALCUL_PARAMETRAGE:
            case REMISE_MONTANT:
                try {
                    double nouveauMontant = Double.parseDouble(mod.getNouvelleValeur());
                    if (taxation != null) {
                        taxation.setTtc(nouveauMontant);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Montant invalide: " + mod.getNouvelleValeur());
                }
                break;

            case SERVICE_GRATUIT:
                if (taxation != null) {
                    taxation.setTtc(0.0);
                }
                break;

            case MODIFICATION_VALEUR_DECLARE:
                try {
                    double nouvelleValeurDecl = Double.parseDouble(mod.getNouvelleValeur());
                    if (taxation != null) {
                        taxation.setValeurDeclaree(nouvelleValeurDecl);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valeur déclarée invalide: " + mod.getNouvelleValeur());
                }
                break;

            case ANNULATION_ENCAISSEMENT:
                if (taxation != null) {
                    taxation.setFond(0.0);
                    System.out.println("Annulation d'encaissement appliquée.");
                }
                break;

            // --- 3. MODIFICATIONS DE DESTINATION & CLIENT ---
            case ERREUR_DESTINATION_CLIENT:
            case MODIFICATION_DEMANDEE_CLIENT:
                expedition.setAdresseLivraison(mod.getNouvelleValeur());
                break;

            // --- 4. SÉCURITÉ ---
            default:
                System.out.println("Type de modification non géré automatiquement: " + mod.getTypeModification());
                break;
        }

        expeditionRepository.save(expedition);
    }
}
