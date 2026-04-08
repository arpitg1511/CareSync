package com.app.caresync.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
				 
				 // 🚀 Microservice logic: Trust the token! 
				 // We don't call the DB here. We just create a principal from the email.
				 UsernamePasswordAuthenticationToken authentication = 
						 new UsernamePasswordAuthenticationToken(userName, null, null); // Roles can be added here if in JWT
				 
				 SecurityContextHolder.getContext().setAuthentication(authentication);
			 }
		} catch (Exception e) {
			// TODO: handle exception
		}
		
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
