package com.example.backend.controllers;

import com.example.backend.dtos.auth.AuthResponseDTO;
import com.example.backend.dtos.auth.LoginRequestDTO;
import com.example.backend.models.users.User;
import com.example.backend.security.jwt.JwtUtil;
import com.example.backend.security.services.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

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
}
