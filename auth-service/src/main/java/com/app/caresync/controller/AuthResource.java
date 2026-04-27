package com.app.caresync.controller;

import com.app.caresync.model.User;
import com.app.caresync.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        return ResponseEntity.ok(authService.login(email, password));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.refreshToken(token));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<User> updateProfile(@PathVariable Long userId, @RequestBody User user) {
        return ResponseEntity.ok(authService.updateProfile(userId, user));
    }

    @PostMapping("/password")
    public ResponseEntity<String> changePassword(@RequestParam Long userId, @RequestBody Map<String, String> passwords) {
        String newPassword = passwords.get("newPassword");
        authService.changePassword(userId, newPassword);
        return ResponseEntity.ok("Password changed");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<String> deactivateAccount(@RequestParam Long userId) {
        authService.deactivateAccount(userId);
        return ResponseEntity.ok("Account deactivated");
    }

    // 🔐 Internal endpoint for Feign Clients
    @GetMapping("/internal/users/email/{email}")
    public ResponseEntity<User> getUserByEmailInternal(@PathVariable String email) {
        return ResponseEntity.ok(authService.getUserByEmail(email));
    }
}
