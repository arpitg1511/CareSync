package com.app.caresync.security;

import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException{
		
		try {
			 String jwt = parseJwt(request);
			 if(jwt != null && jwtUtils.validateJwtToken(jwt)) {
				 String userName = jwtUtils.getUserNameFromJwtToken(jwt);
				 String role = jwtUtils.getRoleFromJwtToken(jwt);
                
                // 🛡️ Null-Safe & Prefix-Smart Authority Extraction
                String finalRole = (role != null) 
                    ? (role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase())
                    : "ROLE_PATIENT";
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userName, null, 
                                Collections.singletonList(new SimpleGrantedAuthority(finalRole)));
				 
				 SecurityContextHolder.getContext().setAuthentication(authentication);
			 }
		} catch (Exception e) {}
		
		filterChain.doFilter(request, response);
	}
	
	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}
		return null;
	}
}
