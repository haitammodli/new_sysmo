package com.example.backend.services;

import com.example.backend.dtos.users.UserResponseDTO;
import com.example.backend.enums.Roles;
import com.example.backend.models.users.Client;
import com.example.backend.models.users.User;
import com.example.backend.repositories.AgenceRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgenceRepository agenceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);
        return users.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public Optional<UserResponseDTO> updateUser(Long id, Map<String, Object> updates) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (updates.containsKey("nom")) user.setNom((String) updates.get("nom"));
        if (updates.containsKey("prenom")) user.setPrenom((String) updates.get("prenom"));
        if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
        if (updates.containsKey("role")) {
            user.setRole(Roles.valueOf(((String) updates.get("role")).toUpperCase()));
        }

        if (updates.containsKey("password")) {
            String newPassword = (String) updates.get("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }
        }

        if (user instanceof Client) {
            Client client = (Client) user;
            if (updates.containsKey("adresse")) client.setAdresse((String) updates.get("adresse"));
            if (updates.containsKey("telephone")) client.setTelephone((String) updates.get("telephone"));
            if (updates.containsKey("secteur")) client.setSecteur((String) updates.get("secteur"));
            if (updates.containsKey("listeNoire")) client.setListeNoire((Boolean) updates.get("listeNoire"));
            if (updates.containsKey("motifListeNoire")) client.setMotifListeNoire((String) updates.get("motifListeNoire"));
        } else if (user instanceof com.example.backend.models.users.ChefAgence) {
            com.example.backend.models.users.ChefAgence chef = (com.example.backend.models.users.ChefAgence) user;
            if (updates.containsKey("agences")) {
                List<?> agenceIds = (List<?>) updates.get("agences");
                if (agenceIds != null) {
                    List<Long> idsToFetch = agenceIds.stream()
                        .map(idObj -> Long.valueOf(idObj.toString()))
                        .collect(Collectors.toList());
                    List<com.example.backend.models.logistique.Agence> updatedAgences = agenceRepository.findAllById(idsToFetch);
                    
                    updatedAgences.forEach(agence -> agence.setChef(chef));
                    agenceRepository.saveAll(updatedAgences);
                    chef.setAgences(updatedAgences);
                }
            }
        }

        userRepository.save(user);
        return Optional.of(mapToDTO(user));
    }

    public UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setCode(user.getCode());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        if (user instanceof Client) {
            Client client = (Client) user;
            dto.setClient(client.getClient());
            dto.setAdresse(client.getAdresse());
            dto.setTelephone(client.getTelephone());
            dto.setContact(client.getContact());
            dto.setSecteur(client.getSecteur());
            dto.setTypeClient(client.getTypeClient() != null ? client.getTypeClient().name() : null);
            dto.setListeNoire(client.getListeNoire());
            dto.setMotifListeNoire(client.getMotifListeNoire());
        }

        return dto;
    }
}
