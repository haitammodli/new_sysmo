package com.example.backend.services;

import com.example.backend.dtos.auth.AuthResponseDTO;
import com.example.backend.dtos.auth.LoginRequestDTO;
import com.example.backend.dtos.auth.RegisterRequestDTO;
import com.example.backend.enums.Roles;
import com.example.backend.enums.TypeClient;
import com.example.backend.models.logistique.Agence;
import com.example.backend.models.users.*;
import com.example.backend.repositories.AgenceRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.security.jwt.JwtUtil;
import com.example.backend.security.services.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AgenceRepository agenceRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        User user = userDetails.getUser();

        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setToken(jwt);
        responseDTO.setCode(user.getCode());
        responseDTO.setNom(user.getNom());
        responseDTO.setPrenom(user.getPrenom());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole() != null ? user.getRole().name() : null);

        return responseDTO;
    }

    @Transactional
    public void register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = createUserByType(request);

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());

        userRepository.save(user);

        if (user instanceof ChefAgence chef && request.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(request.getAgenceId())
                    .orElseThrow(() -> new RuntimeException("Error: Agence not found!"));
            agence.setChef(chef);
            agenceRepository.save(agence);
        }
    }

    private User createUserByType(RegisterRequestDTO request) {
        return switch (request.getUserType().toUpperCase()) {
            case "ADMIN" -> {
                Admin a = new Admin(); a.setRole(Roles.ADMIN); yield a;
            }
            case "AGENT_MODIF" -> {
                AgentModification am = new AgentModification(); am.setRole(Roles.AGENTMODIFICATION); yield am;
            }
            case "CHEF_AGENCE" -> {
                ChefAgence ca = new ChefAgence(); ca.setRole(Roles.CHEFAGENCE); yield ca;
            }
            case "EXPEDITEUR" -> {
                Expediteur e = new Expediteur(); e.setRole(Roles.EXPEDITEUR); yield e;
            }
            case "DESTINATAIRE" -> {
                Destinataire d = new Destinataire(); d.setRole(Roles.DESTINATAIRE); yield d;
            }
            case "DIRECTION" -> {
                Direction dir = new Direction(); dir.setRole(Roles.DIRECTION); yield dir;
            }
            case "RAMASSEUR" -> {
                Ramasseur r = new Ramasseur(); r.setRole(Roles.RAMASSEUR); yield r;
            }
            case "RESP_MODIF" -> {
                ResponsableModification rm = new ResponsableModification(); rm.setRole(Roles.RESPONSABLEMODIFICATION); yield rm;
            }
            case "CLIENT" -> createClient(request);
            default -> throw new IllegalArgumentException("Error: Unknown user type: " + request.getUserType());
        };
    }

    private Client createClient(RegisterRequestDTO request) {
        Client client = new Client();
        client.setRole(Roles.CLIENT);
        client.setClient(request.getClient());
        client.setAdresse(request.getAdresse());
        client.setTelephone(request.getTelephone());
        client.setContact(request.getContact());
        client.setSecteur(request.getSecteur());
        client.setListeNoire(false);

        if (request.getTypeClient() != null) {
            try {
                client.setTypeClient(TypeClient.valueOf(request.getTypeClient().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error: Unknown typeClient!");
            }
        }
        return client;
    }
}