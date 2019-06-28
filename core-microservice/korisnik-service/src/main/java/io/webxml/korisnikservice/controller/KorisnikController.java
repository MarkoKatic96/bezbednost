package io.webxml.korisnikservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.webxml.korisnikservice.dto.KorisnikDTO;
import io.webxml.korisnikservice.model.Korisnik;
import io.webxml.korisnikservice.security.JwtTokenUtils;
import io.webxml.korisnikservice.service.KorisnikService;
import io.webxml.korisnikservice.validators.Valid;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/korisnik-service")
public class KorisnikController {

	@Autowired
	private KorisnikService korisnikService;
	
	Logger log = LogManager.getLogger(KorisnikController.class);
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	/*
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping("/korisnici")
	public ResponseEntity<List<Korisnik>> getKorisnici(){
		
		List<Korisnik> korisnici = korisnikService.getAllKorisnici();
		return (korisnici!=null) ? new ResponseEntity<List<Korisnik>>(korisnici, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	*/
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping("/korisnici/{id}")
	public ResponseEntity<KorisnikDTO> getKorisnik(@PathVariable("id") Long id){
		Korisnik k = korisnikService.getKorisnikById(id);
		return (k!=null) ? new ResponseEntity<KorisnikDTO>(new KorisnikDTO(k), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping("/korisnik/{email}")
	public ResponseEntity<Korisnik> getKorisnikByEmail(@PathVariable("email") String email){
		System.out.println("korisnik email");
		Korisnik k = korisnikService.getKorisnikByEmail(email);
		return (k!=null) ? new ResponseEntity<Korisnik>(k, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Valid> register(@RequestBody Korisnik korisnik, HttpServletRequest req){
		Valid v = korisnikService.register(korisnik);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Email: {}", "register", req.getRemoteAddr(), req.getMethod(), korisnik.getEmail());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Email: {}", "register", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), korisnik.getEmail());
			
			return new ResponseEntity<Valid>(v, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		Korisnik k = korisnikService.getKorisnikByEmail(korisnik.getEmail());
		
		if(k!=null)
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Email: {}", "register", req.getRemoteAddr(), req.getMethod(), korisnik.getEmail());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Email: {}", "register", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), korisnik.getEmail());
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		else
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Email: {}", "register", req.getRemoteAddr(), req.getMethod(), korisnik.getEmail());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Email: {}", "register", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), korisnik.getEmail());
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/*
	@RequestMapping(value = "/getKorisnikByToken/{token}")
	public ResponseEntity<Korisnik> getKorisnikByToken(@PathVariable("token") String token){
		
		String email = jwtTokenUtils.getUsername(token);
		
		Korisnik korisnik = korisnikService.getKorisnikByEmail(email);
		if (korisnik == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Korisnik>(korisnik, HttpStatus.OK);
	}
	*/
}
