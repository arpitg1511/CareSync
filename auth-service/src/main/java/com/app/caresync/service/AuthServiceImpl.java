package com.app.caresync.service;

import com.app.caresync.dto.*;
import com.app.caresync.exception.EmailAlreadyExistsException;
import com.app.caresync.exception.InvalidPasswordException;
import com.app.caresync.exception.UserNotFoundException;
import com.app.caresync.model.User;
import com.app.caresync.model.UserRole;
import com.app.caresync.repository.UserRepository;
import com.app.caresync.security.JwtUtils;
import com.app.caresync.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getUsername(), role);
    }

    @Autowired
    private com.app.caresync.client.ProviderClient providerClient;

    @Override
    public String registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Error: Email is already in use!");
        }

        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setPasswordHash(encoder.encode(signUpRequest.getPassword()));

        UserRole role = (signUpRequest.getRole() != null) ? 
                UserRole.valueOf(signUpRequest.getRole().toUpperCase()) : UserRole.PATIENT;
        user.setRole(role);

        System.out.println("DEBUG: Registering User with Role: " + role + " (Input was: " + signUpRequest.getRole() + ")");

        User savedUser = userRepository.save(user);

        // 🔗 If the user is a Doctor, notify the provider-service to create a pending profile
        if (role == UserRole.DOCTOR) {
            try {
                System.out.println("🔄 Syncing Doctor Profile to Provider-Service for: " + savedUser.getEmail());
                java.util.Map<String, Object> providerData = new java.util.HashMap<>();
                providerData.put("userId", savedUser.getUserId());
                providerData.put("name", savedUser.getFullName());
                providerData.put("email", savedUser.getEmail());
                
                // Ensure speciality is NEVER null (DB constraint)
                String spec = signUpRequest.getSpeciality();
                if (spec == null || spec.trim().isEmpty()) {
                    spec = "General Medicine";
                }
                providerData.put("speciality", spec);
                
                providerClient.createProviderProfile(providerData);
                System.out.println("✅ Sync Successful for " + savedUser.getEmail() + " with Specialty: " + spec);
            } catch (Exception e) {
                System.err.println("❌ SYNC FAILED: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return "User registered successfully!";
    }

    @Override
    public String changePassword(String email, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        if(!encoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Error : Invalid password");
        }
        
        user.setPasswordHash(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        return "Password updated successfully";
    }

    @Override
    public String deactivateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setActive(false);
        userRepository.save(user);

        return "Account deactivated successfully!";
    }

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Error: Email is already in use!");
        }
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Override
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateToken(authentication);
    }

    @Override
    public void logout(String token) {
        // Typically handled by Spring Security or JWT blacklisting
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validateJwtToken(token);
    }

    @Override
    public String refreshToken(String token) {
        return null; 
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public User updateProfile(Long userId, User user) {
        User existing = getUserById(userId);
        existing.setFullName(user.getFullName());
        existing.setPhone(user.getPhone());
        existing.setProfilePicUrl(user.getProfilePicUrl());
        return userRepository.save(existing);
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deactivateAccount(Long userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }
}
