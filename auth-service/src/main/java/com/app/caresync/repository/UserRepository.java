package com.app.caresync.repository;

import com.app.caresync.model.User;
import com.app.caresync.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA automatically writes the SQL for these!
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findAllByRole(UserRole role);
}
