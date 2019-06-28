package com.megatravel.ratingservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.megatravel.ratingservice.dto.NoviKomentarDTO;
import com.megatravel.ratingservice.dto.RezervacijaDTO;
import com.megatravel.ratingservice.model.Komentar;
import com.megatravel.ratingservice.model.Korisnik;
import com.megatravel.ratingservice.model.StatusKomentara;
import com.megatravel.ratingservice.model.StatusRezervacije;
import com.megatravel.ratingservice.security.JwtTokenUtils;
import com.megatravel.ratingservice.service.KomentarService;
import com.megatravel.ratingservice.validators.KomentarValidator;
import com.megatravel.ratingservice.validators.Valid;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/rating-service/komentar")
public class KomentarController {

	@Autowired
	KomentarService komentarService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	Logger log = LogManager.getLogger(KomentarController.class);

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createKomentar(@RequestBody NoviKomentarDTO noviKomentar, HttpServletRequest req){
		System.out.println("createKomentar()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK || korisnikEntity.getBody()==null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		ResponseEntity<RezervacijaDTO> rezervacijaEntity = restTemplate.getForEntity("https://reservation-service/reservation-service/rezervacija/status/"+noviKomentar.getIdRezervacije(), RezervacijaDTO.class);
		if (rezervacijaEntity.getStatusCode() != HttpStatus.OK) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		RezervacijaDTO rezervacija = rezervacijaEntity.getBody();
		if (rezervacija == null) {		
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		if (rezervacija.getSmestajId()!=noviKomentar.getIdSmestaja() ||
				rezervacija.getRezervacijaId()!=noviKomentar.getIdRezervacije() ||
				rezervacija.getKorisnikId()!=noviKomentar.getIdKorisnika() || rezervacija.getStatusRezervacije()!=StatusRezervacije.POTVRDJENA) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		Komentar komentar = new Komentar(null, rezervacija.getSmestajId(), rezervacija.getRezervacijaId(), rezervacija.getKorisnikId(), noviKomentar.getKomentar(), StatusKomentara.NEOBJAVLJEN);
		
		Valid v = new KomentarValidator().validate(komentar);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		komentar = komentarService.save(komentar);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "createKomentar", req.getHeader("X-FORWARDED-FOR"));
		
		return new ResponseEntity<Komentar>(komentar, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> blokirajObjaviKomentar(@RequestBody StatusKomentara statusKomentara, @PathVariable Long id, HttpServletRequest req){
		System.out.println("blokirajObjaviKomentar(" + statusKomentara + ")");
		
		Optional<Komentar> kom = komentarService.findById(id);
		if (!kom.isPresent()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if (statusKomentara==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(new Valid(false, "STATUS_NULL"),HttpStatus.UNPROCESSABLE_ENTITY); 
		}
		
		if (statusKomentara==StatusKomentara.NEOBJAVLJEN) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(new Valid(false, "STATUS_VALUE"),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Komentar komentar = kom.get();
		komentar.setStatus(statusKomentara);
		komentar = komentarService.save(komentar);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "blokirajObjaviKomentar", req.getHeader("X-FORWARDED-FOR"));
		
		return new ResponseEntity<Komentar>(komentar, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/neprocitane", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Komentar>> getNeobjavljeniKomentari(Pageable page) {
		System.out.println("getNeobjavljeniKomentari()");
		
		if (page==null) {
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Page<Komentar> neobjavljeniKomentari = komentarService.findAllNeobjavljenji(page);
		
		HttpHeaders headers = new HttpHeaders();
		long komentariTotal = neobjavljeniKomentari.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(komentariTotal));

		return new ResponseEntity<>(neobjavljeniKomentari.getContent(), headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/all/{smestajId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Komentar>> getAllKomentari(@PathVariable Long smestajId, Pageable page) {
		Page<Komentar> allKomentari = komentarService.findAllObjavljenji(smestajId, page);
		
		HttpHeaders headers = new HttpHeaders();
		long komentariTotal = allKomentari.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(komentariTotal));

		return new ResponseEntity<>(allKomentari.getContent(), headers, HttpStatus.OK);
	}
	
}
