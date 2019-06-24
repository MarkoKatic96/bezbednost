package io.webxml.reservationservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import io.webxml.reservationservice.dto.RezervacijaDTO;
import io.webxml.reservationservice.model.Korisnik;
import io.webxml.reservationservice.model.Rezervacija;
import io.webxml.reservationservice.model.RezervacijeRestTemplate;
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
@CrossOrigin(origins = "http://localhost:3000")
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
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	//ovo samo admin moze da vidi. Treba zastititi
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/rezervacije")
	public ResponseEntity<RezervacijeRestTemplate> getAllReservations(){
		List<Rezervacija> rezervacije = rezervacijaService.getAllReservations();
		RezervacijeRestTemplate rrt = new RezervacijeRestTemplate();
		rrt.setRezervacijaList(rezervacije);
		return (!rezervacije.isEmpty()) ? new ResponseEntity<RezervacijeRestTemplate>(rrt, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/samostalneRezervacije")
	public ResponseEntity<SamostalnaRezervacijaRestTemplate> getAllAgentReservations(){
		List<SamostalnaRezervacija> rezervacije = srs.findAll();
		SamostalnaRezervacijaRestTemplate rrt = new SamostalnaRezervacijaRestTemplate();
		rrt.setSamostalnaRezervacijaList(rezervacije);
		return (!rezervacije.isEmpty()) ? new ResponseEntity<SamostalnaRezervacijaRestTemplate>(rrt, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/rezervacije/{token}")
	public ResponseEntity<List<Rezervacija>> getAllReservationsFromUser(@PathVariable("token") String token){
		
		Korisnik k = restTemplate.getForObject("http://korisnik-service/korisnik-service/getKorisnikByToken/" + token, Korisnik.class);
		
		if(k!=null) {
			List<Rezervacija> rezervacije = rezervacijaService.getAllReservationsFromUser(k.getIdKorisnik());
			return (!rezervacije.isEmpty()) ? new ResponseEntity<List<Rezervacija>>(rezervacije, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	//ovako radi ali vrv nije dobra praksa ovako raditi
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping(value = "/rezervisi/{token}", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> reserve(@PathVariable("token") String token, @RequestBody Rezervacija rezervacija){
		
		Valid v = new RezervacijaValidator().validate(rezervacija);
		if (!v.isValid()) {
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		if (rezervacija.getStatusRezervacije()!=StatusRezervacije.KREIRANA) {
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Rezervacija r = rezervacijaService.reserve(rezervacija);
		return (r!=null) ? new ResponseEntity<Rezervacija>(r, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);	
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping(value = "/otkazi/{id}/{token}", method = RequestMethod.DELETE)
	public ResponseEntity<Rezervacija> otkazi(@PathVariable("id") Long id, @PathVariable("token") String token, HttpServletRequest req){
		
		Korisnik k = restTemplate.getForObject("http://korisnik-service/korisnik-service/getKorisnikByToken/" + token, Korisnik.class);

		if(k==null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Rezervacija r = rezervacijaService.otkaziRezervaciju(id, k.getIdKorisnik());
		return (r!=null) ? new ResponseEntity<Rezervacija>(r, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/rezervacija/status/{id}", method = RequestMethod.GET)
	public ResponseEntity<RezervacijaDTO> wtf(@PathVariable("id") Long id){
		
		Rezervacija r = rr.getOne(id);
		if (r!=null) {
			return new ResponseEntity<RezervacijaDTO>(new RezervacijaDTO(r), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		
	}
}
