package io.webxml.reservationservice.controller;

import java.util.List;

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
import org.springframework.web.client.RestTemplate;

import io.webxml.reservationservice.model.Korisnik;
import io.webxml.reservationservice.model.Rezervacija;
import io.webxml.reservationservice.model.SamostalnaRezervacija;
import io.webxml.reservationservice.model.SamostalnaRezervacijaRestTemplate;
import io.webxml.reservationservice.model.StatusRezervacije;
import io.webxml.reservationservice.repository.RezervacijaRepository;
import io.webxml.reservationservice.repository.SamostalnaRezervacijaRepository;
import io.webxml.reservationservice.security.JwtTokenUtils;
import io.webxml.reservationservice.service.RezervacijaService;
import io.webxml.reservationservice.validators.RezervacijaValidator;
import io.webxml.reservationservice.validators.Valid;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/reservation-service")
public class RezervacijaController {
	
	@Autowired
	private RezervacijaService rezervacijaService;
	
	@Autowired
	SamostalnaRezervacijaRepository srs;
	
	@Autowired
	RezervacijaRepository rr;
	
	@Autowired
	private RestTemplate restTemplate;
	
	Logger log = LogManager.getLogger(RezervacijaController.class);
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	/*
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping(value = "/rezervacije")
	public ResponseEntity<RezervacijeRestTemplate> getAllReservations(){
		List<Rezervacija> rezervacije = rezervacijaService.getAllReservations();
		RezervacijeRestTemplate rrt = new RezervacijeRestTemplate();
		rrt.setRezervacijaList(rezervacije);
		return (!rezervacije.isEmpty()) ? new ResponseEntity<RezervacijeRestTemplate>(rrt, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	*/
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/samostalneRezervacije")
	public ResponseEntity<SamostalnaRezervacijaRestTemplate> getAllAgentReservations(){
		List<SamostalnaRezervacija> rezervacije = srs.findAll();
		SamostalnaRezervacijaRestTemplate rrt = new SamostalnaRezervacijaRestTemplate();
		rrt.setSamostalnaRezervacijaList(rezervacije);
		return (!rezervacije.isEmpty()) ? new ResponseEntity<SamostalnaRezervacijaRestTemplate>(rrt, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/rezervacije")
	public ResponseEntity<List<Rezervacija>> getAllReservationsFromUser(HttpServletRequest req){
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getAllReservationsFromUser", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getAllReservationsFromUser", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return null;
		}
		
		Korisnik korisnik = korisnikEntity.getBody();
		if (korisnik == null) {	
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getAllReservationsFromUser", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getAllReservationsFromUser", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		List<Rezervacija> rezervacije = rezervacijaService.getAllReservationsFromUser(korisnik.getIdKorisnik());
		return (!rezervacije.isEmpty()) ? new ResponseEntity<List<Rezervacija>>(rezervacije, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/rezervisi", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> reserve(@RequestBody Rezervacija rezervacija, HttpServletRequest req){
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK || korisnikEntity.getBody()==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getRemoteAddr(), req.getMethod(), rezervacija.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezervacija.getTimestamp());
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		Valid v = new RezervacijaValidator().validate(rezervacija);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getRemoteAddr(), req.getMethod(), rezervacija.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezervacija.getTimestamp());
			
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		rezervacija.setStatusRezervacije(StatusRezervacije.KREIRANA);
		rezervacija.setKorisnikId(korisnikEntity.getBody().getIdKorisnik());
		Rezervacija r = rezervacijaService.reserve(rezervacija);
		
		if(r != null)
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getRemoteAddr(), req.getMethod(), rezervacija.getTimestamp());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezervacija.getTimestamp());
			
			return  new ResponseEntity<Rezervacija>(r, HttpStatus.OK);
		}
		else
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getRemoteAddr(), req.getMethod(), rezervacija.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "reserve", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezervacija.getTimestamp());
			
			return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	@RequestMapping(value = "/otkazi/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Rezervacija> otkazi(@PathVariable("id") Long id, HttpServletRequest req){
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK || korisnikEntity.getBody()==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "otkazi", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "otkazi", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		Rezervacija r = rezervacijaService.otkaziRezervaciju(id, korisnikEntity.getBody().getIdKorisnik());
		
		if(r != null)
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "otkazi", req.getRemoteAddr(), req.getMethod());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "otkazi", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return  new ResponseEntity<Rezervacija>(r, HttpStatus.OK);
		}
		else
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "otkazi", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "otkazi", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	//TODO: Pogledati gde se koristi
	/*
	@RequestMapping(value = "/rezervacija/status/{id}", method = RequestMethod.GET)
	public ResponseEntity<RezervacijaDTO> wtf(@PathVariable("id") Long id){
		
		Rezervacija r = rr.getOne(id);
		if (r!=null) {
			return new ResponseEntity<RezervacijaDTO>(new RezervacijaDTO(r), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	*/
}
