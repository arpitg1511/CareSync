package com.app.caresync.repository;

import com.app.caresync.model.User;
import com.app.caresync.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // PDF: findByEmail(), findByUserId(), existsByEmail()
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Boolean existsByEmail(String email);

    // PDF: findAllByRole()
    List<User> findAllByRole(UserRole role);

    // PDF: findByPhone()
    Optional<User> findByPhone(String phone);

    // PDF: findByFullNameContaining()
    List<User> findByFullNameContaining(String name);

    // PDF: deleteByUserId() - provided by JpaRepository.deleteById()
}
