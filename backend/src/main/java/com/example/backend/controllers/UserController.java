package com.example.backend.controllers;

import com.example.backend.dtos.users.UserResponseDTO;
import com.example.backend.enums.Roles;
import com.example.backend.models.users.Client;
import com.example.backend.models.users.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userService.mapToDTO(userOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userService.mapToDTO(userOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            Roles roleEnum = Roles.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(roleEnum);
            List<UserResponseDTO> responseDTOs = users.stream()
                    .map(userService::mapToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: Invalid role provided!");
        }
    }

    @Autowired
    private com.example.backend.services.UserService userService;

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    @GetMapping("/agence/{agenceId}")
    public ResponseEntity<?> getUsersByAgence(@PathVariable Long agenceId) {
        List<User> users = userRepository.findByAgenceId(agenceId);
        List<UserResponseDTO> responseDTOs = users.stream()
                .map(userService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/clients/blacklisted")
    public ResponseEntity<?> getBlacklistedClients() {
        List<Client> clients = userRepository.findBlacklistedClients();
        List<UserResponseDTO> responseDTOs = clients.stream()
                .map(client -> userService.mapToDTO((User) client))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_MODIFICATION')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_MODIFICATION')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody java.util.Map<String, Object> updates) {
        Optional<UserResponseDTO> updatedUser = userService.updateUser(id, updates);
        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser.get());
        }
        return ResponseEntity.notFound().build();
    }
}
