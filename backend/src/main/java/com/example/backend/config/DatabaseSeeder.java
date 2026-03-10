package com.example.backend.config;

import com.example.backend.enums.Roles;
import com.example.backend.models.users.Admin;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Autowired
    private com.example.backend.repositories.AgenceRepository agenceRepository;

    @Autowired
    private com.example.backend.repositories.ReferenceDataRepository referenceDataRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) throws Exception {
        
        try {
            entityManager.createNativeQuery("ALTER TABLE utilisateurs ALTER COLUMN liste_noire DROP NOT NULL").executeUpdate();
            System.out.println("Dropped NOT NULL constraint on liste_noire successfully.");
        } catch (Exception e) {
            System.out.println("Constraint might already be dropped or table missing: " + e.getMessage());
        }

        // --- 1. ADMIN USER ---
        String adminEmail = "haytam.modli2004@gmail.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Admin admin = new Admin();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Roles.ADMIN);
            admin.setNom("Super");
            admin.setPrenom("Admin");
            userRepository.save(admin);
            System.out.println("Default Admin user created: " + adminEmail);
        } else {
            System.out.println("Admin user already exists.");
        }

        // --- 2. AGENCE ---
        com.example.backend.models.logistique.Agence agenceCentrale = new com.example.backend.models.logistique.Agence();
        agenceCentrale.setLibelle("Agence Centrale");
        agenceCentrale.setVille("Casablanca");
        agenceCentrale = agenceRepository.save(agenceCentrale);
        System.out.println(">>> SEEDED AGENCE ID: " + agenceCentrale.getCode());

        // --- 3. CLIENT ---
        String clientEmail = "client@test.com";
        com.example.backend.models.users.Client testClient = null;
        if (userRepository.findByEmail(clientEmail).isEmpty()) {
            testClient = new com.example.backend.models.users.Client();
            testClient.setEmail(clientEmail);
            testClient.setPassword(passwordEncoder.encode("client123"));
            testClient.setRole(Roles.CLIENT);
            testClient.setNom("Test");
            testClient.setPrenom("Client");
            testClient.setClient("Entreprise X");
            testClient.setTypeClient(com.example.backend.enums.TypeClient.ENTREPRISE);
            testClient = userRepository.save(testClient);
        } else {
            testClient = (com.example.backend.models.users.Client) userRepository.findByEmail(clientEmail).get();
        }
        System.out.println(">>> SEEDED CLIENT ID: " + testClient.getCode());

        // --- 4. REFERENCE DATA ---
        com.example.backend.models.systeme.ReferenceData nature = new com.example.backend.models.systeme.ReferenceData();
        nature.setCategorie("NATURE_EXPEDITION");
        nature.setLibelle("Document");
        nature = referenceDataRepository.save(nature);
        System.out.println(">>> SEEDED REFERENCE DATA (Nature) ID: " + nature.getId());

        com.example.backend.models.systeme.ReferenceData type = new com.example.backend.models.systeme.ReferenceData();
        type.setCategorie("TYPE_EXPEDITION");
        type.setLibelle("Express");
        type = referenceDataRepository.save(type);
        System.out.println(">>> SEEDED REFERENCE DATA (Type) ID: " + type.getId());
    }
}
