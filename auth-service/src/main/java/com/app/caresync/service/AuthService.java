package com.app.caresync.service;

import com.app.caresync.model.User;

public interface AuthService {
    User register(User user);
    String login(String email, String password);
    void logout(String token);
    boolean validateToken(String token);
    String refreshToken(String token);
    User getUserByEmail(String email);
    User getUserById(Long userId);
    User updateProfile(Long userId, User user);
    void changePassword(Long userId, String newPassword);
    void deactivateAccount(Long userId);
}
