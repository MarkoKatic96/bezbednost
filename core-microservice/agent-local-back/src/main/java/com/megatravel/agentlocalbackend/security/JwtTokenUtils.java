package com.megatravel.agentlocalbackend.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenUtils {
	@Value("${jwt.security.key}")
    private String jwtKey;

	@Autowired
	private MyUserDetails myUserDetails;

	Authentication getAuthentication(String token) {
		UserDetails userDetails = myUserDetails.loadUserByUsername(token);
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails == null ? null : userDetails.getAuthorities());
	}	

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
	
	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody().getSubject();
	}

	boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

}