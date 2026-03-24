package com.example.backend.controllers;

import com.example.backend.dtos.auth.AuthResponseDTO;
import com.example.backend.dtos.auth.LoginRequestDTO;
import com.example.backend.dtos.auth.RegisterRequestDTO;
import com.example.backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        // If authentication fails,automatically throws an exception
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {
            // Catches mapping errors
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Catches business logic errors
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}