package com.megatravel.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megatravel.authservice.dao.UserDAO;
import com.megatravel.authservice.model.LoginUser;
import com.megatravel.authservice.model.Rola;
import com.megatravel.authservice.model.User;
import com.megatravel.authservice.model.users.Admin;
import com.megatravel.authservice.model.users.Agent;
import com.megatravel.authservice.model.users.Korisnik;
import com.megatravel.authservice.repository.AdminRepository;
import com.megatravel.authservice.repository.AgentRepository;
import com.megatravel.authservice.repository.KorisnikRepository;
import com.megatravel.authservice.repository.RevokedTokensRepository;
import com.megatravel.authservice.repository.RolaRepository;
import com.megatravel.authservice.security.JwtTokenUtil;
import com.megatravel.authservice.validators.LoginUserValidator;
import com.megatravel.authservice.validators.Valid;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/auth/login")
public class LoginController {

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
    private JwtTokenUtil jwtTokenUtil;
    
	@Autowired
    private AdminRepository adminRepository;
	
	@Autowired
    private AgentRepository agentRepository;
	
	@Autowired
    private KorisnikRepository korisnikRepository;
	
	@Autowired
	private RevokedTokensRepository revokedTokensRepository;
	
	@Autowired
	private RolaRepository rolaRepository;
    
	@PostMapping("/admin")
    public ResponseEntity<String> loginAdmin(@RequestBody LoginUser loginUser) throws Exception{
    	System.out.println("neki dobar token za admina");

    	Valid v = new LoginUserValidator().validate(loginUser);
		if (!v.isValid()) {
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
    	
    	final Admin user = adminRepository.findByEmailPassword(loginUser.getEmail().trim(), passwordEncoder.encode(loginUser.getLozinka().trim()));
        if (user==null) {
        	return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        Rola rola = rolaRepository.findByNazivRole("ROLE_ADMIN");
        final String token = jwtTokenUtil.doGenerateToken(new User(user, rola));
        
        return ResponseEntity.ok(token);
    }
	
	@PostMapping("/agent")
    public ResponseEntity<String> loginAgent(@RequestBody LoginUser loginUser) throws Exception{
    	System.out.println("neki dobar token za agenta");

    	Valid v = new LoginUserValidator().validate(loginUser);
		if (!v.isValid()) {
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		final Agent user = agentRepository.findByEmailPassword(loginUser.getEmail(), passwordEncoder.encode(loginUser.getLozinka()));
        if (user==null) {
        	return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        Rola rola = rolaRepository.findByNazivRole("ROLE_AGENT");
        final String token = jwtTokenUtil.doGenerateToken(new User(user, rola));
        
        return ResponseEntity.ok(token);
    }
	
	@PostMapping("/korisnik")
    public ResponseEntity<String> loginKorisnik(@RequestBody LoginUser loginUser) throws Exception{
    	System.out.println("neki dobar token za korisnika");

    	Valid v = new LoginUserValidator().validate(loginUser);
		if (!v.isValid()) {
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		final Korisnik user = korisnikRepository.findByEmailPassword(loginUser.getEmail(), passwordEncoder.encode(loginUser.getLozinka()));
        if (user==null) {
        	return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        Rola rola = rolaRepository.findByNazivRole("ROLE_KORISNIK");
        final String token = jwtTokenUtil.doGenerateToken(new User(user, rola));
        
        return ResponseEntity.ok(token);
    }
	
	@PostMapping("/role")
	public ResponseEntity<UserDAO> roleKorisnika(@RequestBody String token){
		String rola = "";
		String lozinka = "";
		
		if (revokedTokensRepository.findOne(token)!=null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		String email = jwtTokenUtil.getUsernameFromToken(token);
		if (email.isEmpty() || email.equals("")) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		if (korisnikRepository.findByEmail(email)!=null) {
        	rola = "ROLE_KORISNIK";
        	lozinka = korisnikRepository.findByEmail(email).getLozinka();
        } else if (agentRepository.findByEmail(email)!=null) {
        	rola = "ROLE_AGENT";
        	lozinka = agentRepository.findByEmail(email).getLozinka();
        } else if (adminRepository.findByEmail(email)!=null) {
        	rola = "ROLE_ADMIN";
        	lozinka = adminRepository.findByEmail(email).getLozinka();
        } else {
        	return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
		
		return new ResponseEntity<UserDAO>(new UserDAO(email, lozinka, rola), HttpStatus.OK);
	}
    
    @GetMapping("/ping")
    private ResponseEntity<String> ping() {
		return new ResponseEntity<String>("You reached auth service: ping", HttpStatus.OK);
	}
    
}
