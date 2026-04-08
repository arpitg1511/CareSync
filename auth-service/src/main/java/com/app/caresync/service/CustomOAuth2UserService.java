package com.app.caresync.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.app.caresync.model.User;
import com.app.caresync.model.UserRole;
import com.app.caresync.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userrequest) throws OAuth2AuthenticationException {
		
		OAuth2User oAuth2User = super.loadUser(userrequest);
		
		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");
		
		// 2. Check if user already exists in our MySQL
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            // 3. If new user, SAVE them to our database!
            User user = new User();
            user.setEmail(email);
            user.setFullName(name != null ? name : email);
            user.setRole(UserRole.PATIENT); // Default role
            user.setPasswordHash(""); // No password needed for Social Login
            
            userRepository.save(user);
        }
        
        return oAuth2User;
	}
	
}
