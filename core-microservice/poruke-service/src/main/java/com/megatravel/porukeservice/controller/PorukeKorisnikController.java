package com.megatravel.porukeservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;

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

import com.megatravel.porukeservice.dto.NovaPorukaDTO;
import com.megatravel.porukeservice.dto.PorukaDTO;
import com.megatravel.porukeservice.model.Korisnik;
import com.megatravel.porukeservice.model.Poruka;
import com.megatravel.porukeservice.model.StatusPoruke;
import com.megatravel.porukeservice.model.TipOsobe;
import com.megatravel.porukeservice.security.JwtTokenUtils;
import com.megatravel.porukeservice.service.PorukeService;
import com.megatravel.porukeservice.validators.Valid;

@RestController
@RequestMapping("/poruke-korisnik-service/poruke")
@CrossOrigin(origins = "https://localhost:3000")
public class PorukeKorisnikController {
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	@Autowired
	PorukeService porukeService;
	
	@Autowired
	RestTemplate restTemplate;
	
	Logger log = LogManager.getLogger(PorukeKorisnikController.class);
	
	@RequestMapping(value = "/{agentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PorukaDTO>> getPorukeWithAgent(@PathVariable("agentId") Long agentId, HttpServletRequest req, Pageable page) {
		System.out.println("getPorukeWithAgent()");
	
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getPorukeWithAgent", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getPorukeWithAgent", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return null;
		}
		
		Korisnik korisnik = korisnikEntity.getBody();
		if (korisnik == null) {		
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getPorukeWithAgent", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getPorukeWithAgent", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (page==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "getPorukeWithAgent", req.getRemoteAddr(), req.getMethod());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "getPorukeWithAgent", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Page<Poruka> poruke = porukeService.findAllWithAgent(agentId, korisnik.getIdKorisnik(), page);
		
		HttpHeaders headers = new HttpHeaders();
		long porukeTotal = poruke.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(porukeTotal));

		List<PorukaDTO> retVal = new ArrayList<>();
		
		for (Poruka p : poruke) {
			retVal.add(new PorukaDTO(p));
		}
		
		return new ResponseEntity<>(retVal, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/neprocitane", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PorukaDTO>> getNeprocitanePoruke(Pageable page, HttpServletRequest req) {
		System.out.println("getNeprocitanePoruke()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK) {
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getNeprocitanePoruke", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getNeprocitanePoruke", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return null;
		}
		
		Korisnik korisnik = korisnikEntity.getBody();
		if (korisnik == null) {		
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getNeprocitanePoruke", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "getNeprocitanePoruke", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Page<Poruka> poruke = porukeService.findAllNeprocitaneZaKorisnika(korisnik.getIdKorisnik(), page);
		
		HttpHeaders headers = new HttpHeaders();
		long porukeTotal = poruke.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(porukeTotal));

		List<PorukaDTO> retVal = new ArrayList<>();
		
		for (Poruka p : poruke) {
			retVal.add(new PorukaDTO(p));
		}
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "getNeprocitanePoruke", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "getNeprocitanePoruke", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
		
		
		return new ResponseEntity<>(retVal, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{agentId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> setProcitanePorukeFromAgent(@PathVariable Long agentId, HttpServletRequest req) {
		System.out.println("setProcitanePorukeFromAgent()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "setProcitanePorukeFromAgent", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "setProcitanePorukeFromAgent", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return null;
		}
		
		Korisnik korisnik = korisnikEntity.getBody();
		if (korisnik == null) {		
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "setProcitanePorukeFromAgent", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "setProcitanePorukeFromAgent", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		List<Poruka> poruke = porukeService.findAllNeprocitaneWithAgent(agentId, korisnik.getIdKorisnik());
		
		for (Poruka p : poruke) {
			p.setStatus(StatusPoruke.PROCITANA);
			porukeService.save(p);
		}
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "setProcitanePorukeFromAgent", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "setProcitanePorukeFromAgent", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
		
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/posalji", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendPoruka(@RequestBody NovaPorukaDTO novaPoruka, HttpServletRequest req) {
		System.out.println("sendPoruka()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service/korisnik/"+email, Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Receiver: {}", "sendPoruka", req.getRemoteAddr(), req.getMethod(), novaPoruka.getPrimalac());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Receiver: {}", "sendPoruka", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaPoruka.getPrimalac());
			
			
			return null;
		}
		
		Korisnik korisnik = korisnikEntity.getBody();
		if (korisnik == null) {			
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Receiver: {}", "sendPoruka", req.getRemoteAddr(), req.getMethod(), novaPoruka.getPrimalac());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Receiver: {}", "sendPoruka", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaPoruka.getPrimalac());
			
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Poruka poruka = new Poruka(null, korisnik.getIdKorisnik(), TipOsobe.KORISNIK, novaPoruka.getPrimalac(), TipOsobe.AGENT, novaPoruka.getSadrzaj(), StatusPoruke.POSLATA);
		if (!Pattern.matches("[\\p{L}\\p{M}]+", poruka.getSadrzaj())) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Receiver: {}", "sendPoruka", req.getRemoteAddr(), req.getMethod(), novaPoruka.getPrimalac());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Receiver: {}", "sendPoruka", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaPoruka.getPrimalac());
			
			return new ResponseEntity<>(new Valid(false, "PORUKA_CHAR"), HttpStatus.UNPROCESSABLE_ENTITY);
			
		}
		
		Poruka retVal = porukeService.save(poruka);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "sendPoruka", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "sendPoruka", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
		
		
		return new ResponseEntity<>(new PorukaDTO(retVal), HttpStatus.CREATED);
	}
	
}
