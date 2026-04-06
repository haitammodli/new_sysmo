package com.example.backend.repositories;

import com.example.backend.models.users.User;
import com.example.backend.models.users.Client;
import com.example.backend.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Roles role);
    List<User> findByRoleIn(List<Roles> roles);

    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String prenom, String email);

    @Query("SELECT c FROM Client c WHERE c.listeNoire = true")
    List<Client> findBlacklistedClients();

    @Query("SELECT u FROM User u WHERE u.code IN (SELECT c.code FROM ChefAgence c JOIN c.agences a WHERE a.code = :agenceId)")
    List<User> findByAgenceId(@Param("agenceId") Long agenceId);
}
