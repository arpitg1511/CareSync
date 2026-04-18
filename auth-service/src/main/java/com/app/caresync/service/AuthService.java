package com.app.caresync.service;

import com.app.caresync.dto.*;
import com.app.caresync.model.User;

public interface AuthService {
    // DTO based methods (used by AuthController)
    JwtResponse authenticateUser(LoginRequest loginRequest);
    String registerUser(SignupRequest signUpRequest);
    String changePassword(String email, PasswordChangeRequest request);
    String deactivateUser(String email);

    // Entity/ID based methods (used by AuthResource/Internal)
    User register(User user);
    String login(String email, String password);
    void logout(String token);
    boolean validateToken(String token);
    String refreshToken(String token);
    User getUserByEmail(String email);
    User getUserById(Long userId);
    User updateProfile(String email, UserDTO updates);
    User updateProfile(Long userId, User user);
    void changePassword(Long userId, String newPassword);
    void deactivateAccount(Long userId);
}
