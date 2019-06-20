package com.megatravel.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megatravel.authservice.config.JwtTokenUtil;
import com.megatravel.authservice.model.LoginUser;
import com.megatravel.authservice.model.Rola;
import com.megatravel.authservice.model.User;
import com.megatravel.authservice.model.users.Admin;
import com.megatravel.authservice.model.users.Agent;
import com.megatravel.authservice.model.users.Korisnik;
import com.megatravel.authservice.repository.AdminRepository;
import com.megatravel.authservice.repository.AgentRepository;
import com.megatravel.authservice.repository.KorisnikRepository;
import com.megatravel.authservice.repository.RolaRepository;

@RestController
@RequestMapping("/auth/login")
public class UserController {
    
	//@Autowired
    //private AuthenticationManager authenticationManager;
    
	@Autowired
    private JwtTokenUtil jwtTokenUtil;
    
	@Autowired
    private AdminRepository adminRepository;
	
	@Autowired
    private AgentRepository agentRepository;
	
	@Autowired
    private KorisnikRepository korisnikRepository;
	
	@Autowired
	private RolaRepository rolaRepository;
    
	@PostMapping("/admin")
    public ResponseEntity<String> loginAdmin(@RequestBody LoginUser loginUser) throws Exception{
    	System.out.println("neki dobar token za admina");

    	final Admin user = adminRepository.findByEmailPassword(loginUser.getEmail(), loginUser.getLozinka());
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

    	//final Authentication authentication = authenticationManager.authenticate(
        	//	new UsernamePasswordAuthenticationToken(loginUser.getEmail(),loginUser.getLozinka()));
        
        //SecurityContextHolder.getContext().setAuthentication(authentication);
    	final Agent user = agentRepository.findByEmailPassword(loginUser.getEmail(), loginUser.getLozinka());
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

    	//final Authentication authentication = authenticationManager.authenticate(
        	//	new UsernamePasswordAuthenticationToken(loginUser.getEmail(),loginUser.getLozinka()));
        
        //SecurityContextHolder.getContext().setAuthentication(authentication);
    	final Korisnik user = korisnikRepository.findByEmailPassword(loginUser.getEmail(), loginUser.getLozinka());
        if (user==null) {
        	return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        Rola rola = rolaRepository.findByNazivRole("ROLE_KORISNIK");
        final String token = jwtTokenUtil.doGenerateToken(new User(user, rola));
        
        return ResponseEntity.ok(token);
    }
    
    @GetMapping("/ping")
    private ResponseEntity<String> ping() {
		return new ResponseEntity<String>("You reached auth service: ping", HttpStatus.OK);
	}
    
}
