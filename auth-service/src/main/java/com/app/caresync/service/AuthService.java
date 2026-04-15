package com.app.caresync.service;

import com.app.caresync.dto.JwtResponse;
import com.app.caresync.dto.LoginRequest;
import com.app.caresync.dto.PasswordChangeRequest;
import com.app.caresync.dto.SignupRequest;
import com.app.caresync.model.User;
import com.app.caresync.model.UserRole;
import com.app.caresync.repository.UserRepository;
import com.app.caresync.security.JwtUtils;
import com.app.caresync.security.UserDetailsImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    // 🚪 Logic for Login
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getUsername(), role);
    }

    // 📝 Logic for Registration
    public String registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setPasswordHash(encoder.encode(signUpRequest.getPassword()));

        UserRole role = (signUpRequest.getRole() != null) ? 
                UserRole.valueOf(signUpRequest.getRole().toUpperCase()) : UserRole.PATIENT;
        user.setRole(role);

        userRepository.save(user);
        return "User registered successfully!";
    }
    
    // Password Change
    public String changePassword(String email, PasswordChangeRequest request) {
    	
    	User user = userRepository.findByEmail(email)
    			.orElseThrow(() -> new RuntimeException("User not found"));
    	
    	if(!encoder.matches(request.getOldPassword(), user.getPasswordHash())) {
    		throw new RuntimeException("Error : Invalid password");
    	}
    	
    	user.setPasswordHash(encoder.encode(request.getNewPassword()));
    	userRepository.save(user);
    	
    	return "Password updated successfully";
    }
    
 // 🛡️ Deactivate Account Logic
    public String deactivateUser(String email) {
        // 1. Find the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Turn off the account
        user.setActive(false);
        userRepository.save(user);

        return "Account deactivated successfully!";
    }

}
