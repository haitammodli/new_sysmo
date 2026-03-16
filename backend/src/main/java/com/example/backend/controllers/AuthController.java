package com.example.backend.controllers;

import com.example.backend.dtos.auth.AuthResponseDTO;
import com.example.backend.dtos.auth.LoginRequestDTO;
import com.example.backend.dtos.auth.RegisterRequestDTO;
import com.example.backend.models.users.*;
import com.example.backend.enums.Roles;
import com.example.backend.enums.TypeClient;
import com.example.backend.models.logistique.Agence;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.AgenceRepository;
import java.util.Optional;
import com.example.backend.security.jwt.JwtUtil;
import com.example.backend.security.services.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AgenceRepository agenceRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
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
        responseDTO.setRole(user.getRole() != null ? user.getRole().name() : null);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user;
        switch (registerRequest.getUserType()) {
            case "ADMIN":
                user = new Admin();
                user.setRole(Roles.ADMIN);
                break;
            case "AGENT_MODIF":
                user = new AgentModification();
                user.setRole(Roles.AGENTMODIFICATION);
                break;
            case "CHEF_AGENCE":
                user = new ChefAgence();
                user.setRole(Roles.CHEFAGENCE);
                break;
            case "EXPEDITEUR":
                user = new Expediteur();
                user.setRole(Roles.EXPEDITEUR);
                break;
            case "DESTINATAIRE":
                user = new Destinataire();
                user.setRole(Roles.DESTINATAIRE);
                break;
            case "CLIENT":
                Client client = new Client();
                client.setClient(registerRequest.getClient());
                client.setAdresse(registerRequest.getAdresse());
                client.setTelephone(registerRequest.getTelephone());
                client.setContact(registerRequest.getContact());
                client.setSecteur(registerRequest.getSecteur());
                if (registerRequest.getTypeClient() != null) {
                    try {
                        client.setTypeClient(TypeClient.valueOf(registerRequest.getTypeClient().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body("Error: Unknown typeClient!");
                    }
                }
                client.setListeNoire(false);
                user = client;
                user.setRole(Roles.CLIENT);
                break;
            case "RESP_MODIF":
                user = new ResponsableModification();
                user.setRole(Roles.RESPONSABLEMODIFICATION);
                break;
            default:
                return ResponseEntity.badRequest().body("Error: Unknown user type!");
        }

        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNom(registerRequest.getNom());
        user.setPrenom(registerRequest.getPrenom());

        userRepository.save(user);

        if (user instanceof ChefAgence && registerRequest.getAgenceId() != null) {
            Optional<Agence> agenceOpt = agenceRepository.findById(registerRequest.getAgenceId());
            if (agenceOpt.isPresent()) {
                Agence agence = agenceOpt.get();
                agence.setChef((ChefAgence) user);
                agenceRepository.save(agence);
            } else {
                return ResponseEntity.badRequest().body("Error: Agence not found!");
            }
        }

        return ResponseEntity.ok("User registered successfully!");
    }
}
