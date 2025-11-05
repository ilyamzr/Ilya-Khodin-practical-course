package org.example.authenticationservice.repository;

import org.example.authenticationservice.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
    Optional<UserRoles> findByUsername(String username);
    boolean existsByUsername(String username);
}
