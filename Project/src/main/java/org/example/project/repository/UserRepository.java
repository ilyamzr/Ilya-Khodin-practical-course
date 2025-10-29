package org.example.project.repository;

import org.example.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("SELECT u FROM User u")
    Page<User> findAllUsers(Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE name LIKE :namePattern", nativeQuery = true)
    Page<User> findByNamePattern(String namePattern, Pageable pageable);
}