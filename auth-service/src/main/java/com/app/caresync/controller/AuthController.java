package com.app.caresync.controller;

import com.app.caresync.dto.*;
import com.app.caresync.model.User;
import com.app.caresync.model.UserRole;
import com.app.caresync.repository.UserRepository;
import com.app.caresync.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    // PDF: /auth/register
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequest req) {
        return ResponseEntity.ok(authService.registerUser(req));
    }

    // PDF: /auth/login
    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.authenticateUser(req));
    }

    // PDF: /auth/logout (stateless JWT - client discards token)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully. Please discard your token."));
    }

    // PDF: /auth/profile GET
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(UserDTO.builder()
                .userId(user.getUserId()).fullName(user.getFullName())
                .email(user.getEmail()).build());
    }

    // PDF: /auth/profile PUT
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody Map<String, String> updates) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (updates.containsKey("fullName")) user.setFullName(updates.get("fullName"));
        if (updates.containsKey("phone")) user.setPhone(updates.get("phone"));
        if (updates.containsKey("profilePicUrl")) user.setProfilePicUrl(updates.get("profilePicUrl"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated"));
    }

    // PDF: /auth/password
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(Authentication auth, @RequestBody PasswordChangeRequest req) {
        return ResponseEntity.ok(authService.changePassword(auth.getName(), req));
    }

    // PDF: /auth/deactivate
    @PutMapping("/deactivate")
    public ResponseEntity<?> deactivate(Authentication auth) {
        return ResponseEntity.ok(authService.deactivateUser(auth.getName()));
    }

    // Internal: get user by email (called by other services)
    @GetMapping("/internal/users/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(UserDTO.builder()
                .userId(user.getUserId()).fullName(user.getFullName()).email(user.getEmail()).build());
    }

    // PDF: Admin - manage all users (view all)
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .map(u -> UserDTO.builder().userId(u.getUserId()).fullName(u.getFullName()).email(u.getEmail()).build())
                .collect(Collectors.toList()));
    }

    // PDF: Admin - suspend user
    @PutMapping("/admin/users/{userId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> suspendUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User suspended"));
    }

    // PDF: Admin - reactivate user
    @PutMapping("/admin/users/{userId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reactivateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User reactivated"));
    }

    // PDF: Admin - delete user
    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    // PDF: Admin - get users by role
    @GetMapping("/admin/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        return ResponseEntity.ok(userRepository.findAllByRole(userRole).stream()
                .map(u -> UserDTO.builder().userId(u.getUserId()).fullName(u.getFullName()).email(u.getEmail()).build())
                .collect(Collectors.toList()));
    }
}
