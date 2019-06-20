package com.megatravel.authservice.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.megatravel.authservice.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
	
	private static final long serialVersionUID = 328311109389616614L;

	static final long EXPIRATIONTIME_ADMIN = 28800000; // 8h
	static final long EXPIRATIONTIME_AGENT = 28800000; // 8h
	static final long EXPIRATIONTIME_KORISNIK = 604800000; // 7 days
	
    @Value("${jwt.security.key}")
    private String jwtKey;
    
    public String doGenerateToken(User subject) throws Exception {
    	Date now = new Date();
        Date validUntil = null;
        
        List<SimpleGrantedAuthority> tipoviKorisnika = new ArrayList<>();
        
        if (subject.getRola().getNazivRole().equals("ROLE_ADMIN")) {
        	validUntil = new Date(now.getTime() + EXPIRATIONTIME_ADMIN);
		} else if (subject.getRola().getNazivRole().equals("ROLE_AGENT")) {
			validUntil = new Date(now.getTime() + EXPIRATIONTIME_AGENT);
		} else if (subject.getRola().getNazivRole().equals("ROLE_KORISNIK")) {
			validUntil = new Date(now.getTime() + EXPIRATIONTIME_KORISNIK);
		} else {
			throw new Exception(new Throwable("- class: JwtTokenUtil \n - method: doGenerateToken(User subject) \n - reason: User role not found!"));
		}
        	
        tipoviKorisnika.add(new SimpleGrantedAuthority(subject.getRola().getNazivRole()));
        
    	Claims claims = Jwts.claims().setSubject(subject.getEmail());
    	claims.put("auth", tipoviKorisnika);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("http://megatravel.com")
                .setIssuedAt(now)
                .setExpiration(validUntil)
                .signWith(SignatureAlgorithm.HS256, jwtKey)
                .compact();
    }
    
	public String getUsernameFromToken(String authToken) {
		return Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(authToken).getBody().getSubject();
	}

	public boolean validateToken(String token) throws JwtException,IllegalArgumentException{
        Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token);
        return true;
	}
}