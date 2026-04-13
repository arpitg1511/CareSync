package com.app.caresync.security;

import org.springframework.stereotype.*;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
	
	@Value("${app.jwt.secret}")
	private String jwtSecret;
	
	@Value("${app.jwt.expiration-ms}")
	private int jwtExpirationMs;
	
	// 1. Generate Token
	public String generateToken(Authentication authentication) {
		
		UserDetails userPrincipal = (UserDetails)
				authentication.getPrincipal();
		
		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.claim("userId", ((UserDetailsImpl) userPrincipal).getId())
				.claim("role", userPrincipal.getAuthorities().iterator().next().getAuthority()) // 🎭 ROLE INCLUDED!
				.setIssuedAt(new Date())
		.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
		.signWith(key(), SignatureAlgorithm.HS256)
		.compact();
	}
	
	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	 // 2. Get Username from Token
	public String getUserNameFromJwtToken(String authToken) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken).getBody().getSubject();
	}
	
	// 3. Validate Token
	public boolean validateJwtToken(String authToken) {
		
		try {
			
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			
			return true;
		} catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			// TODO: handle exception
		}
		
		return false;
	}
}
