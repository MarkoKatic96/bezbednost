package com.megatravel.authservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.megatravel.authservice.model.RevokedTokens;
import com.megatravel.authservice.repository.RevokedTokensRepository;
import com.megatravel.authservice.security.JwtTokenUtil;

@RestController
@RequestMapping("/auth/signout")
public class SignoutController {
	
	@Autowired
    private JwtTokenUtil jwtTokenUtils;

	@Autowired
	private RevokedTokensRepository revokedTokensRepository;
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/agent", method = RequestMethod.GET)
	public ResponseEntity<Void> signoutAgent(HttpServletRequest req) {
		System.out.println("signout()");
		/*
		String email = jwtTokenUtils.resolveTokenToEmail(req);
		
		Agent agent = agentRepository.findByEmail(email);
		if (agent == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		*/
		revokedTokensRepository.save(new RevokedTokens(null, jwtTokenUtils.resolveToken(req)));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public ResponseEntity<Void> signoutAdmin(HttpServletRequest req) {
		System.out.println("signout()");
		/*
		String email = jwtTokenUtils.resolveTokenToEmail(req);
		
		Admin admin = adminRepository.findByEmail(email);
		if (admin == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		*/
		revokedTokensRepository.save(new RevokedTokens(null, jwtTokenUtils.resolveToken(req)));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping(value = "/korisnik", method = RequestMethod.GET)
	public ResponseEntity<Void> signoutKorisnik(HttpServletRequest req) {
		System.out.println("signout()");
		/*
		String email = jwtTokenUtils.resolveTokenToEmail(req);
		
		Korisnik korisnik = korisnikRepository.findByEmail(email);
		if (korisnik == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		*/
		revokedTokensRepository.save(new RevokedTokens(null, jwtTokenUtils.resolveToken(req)));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
