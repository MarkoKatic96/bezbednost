package com.megatravel.porukeservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.megatravel.porukeservice.dto.NovaPorukaDTO;
import com.megatravel.porukeservice.dto.PorukaDTO;
import com.megatravel.porukeservice.model.Agent;
import com.megatravel.porukeservice.model.Korisnik;
import com.megatravel.porukeservice.model.Poruka;
import com.megatravel.porukeservice.model.StatusPoruke;
import com.megatravel.porukeservice.model.TipOsobe;
import com.megatravel.porukeservice.security.JwtTokenUtils;
import com.megatravel.porukeservice.service.PorukeService;
import com.megatravel.porukeservice.validators.Valid;

@RestController
@RequestMapping("/poruke-agent-service/poruke")
public class PorukeAgentController {
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	@Autowired
	PorukeService porukeService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PorukaDTO>> getPorukeWithKorisnik(@PathVariable Long userId, Pageable page, HttpServletRequest req) {
		System.out.println("getPorukeWithKorisnik()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (page==null) {
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Page<Poruka> poruke = porukeService.findAllWithUser(userId, agent.getIdAgenta(), page);
		
		HttpHeaders headers = new HttpHeaders();
		long porukeTotal = poruke.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(porukeTotal));

		List<PorukaDTO> retVal = new ArrayList<>();
		
		for (Poruka p : poruke) {
			retVal.add(new PorukaDTO(p));
		}
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/neprocitane", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PorukaDTO>> getNeprocitanePoruke(Pageable page, HttpServletRequest req) {
		System.out.println("getNeprocitanePoruke()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (page==null) {
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Page<Poruka> poruke = porukeService.findAllNeprocitaneZaAgenta(agent.getIdAgenta(), page);
		
		HttpHeaders headers = new HttpHeaders();
		long porukeTotal = poruke.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(porukeTotal));

		List<PorukaDTO> retVal = new ArrayList<>();
		
		for (Poruka p : poruke) {
			retVal.add(new PorukaDTO(p));
		}
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> setProcitanePorukeFromUser(@PathVariable Long userId, HttpServletRequest req) {
		System.out.println("setProcitanePorukeFromUser()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		String getAgentUrl = "https://agent-global-service/agent-global-service/agent/e/"+email;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+token);

		HttpEntity<String> entity = new HttpEntity<String>(null,headers);
		ResponseEntity<Agent> agentEntity = restTemplate.exchange(getAgentUrl, HttpMethod.POST, entity, Agent.class);
		
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>(agentEntity.getStatusCode());
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		List<Poruka> poruke = porukeService.findAllNeprocitaneWithUser(userId, agent.getIdAgenta());
		
		for (Poruka p : poruke) {
			p.setStatus(StatusPoruke.PROCITANA);
			porukeService.save(p);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendPoruka(@RequestBody NovaPorukaDTO novaPoruka, HttpServletRequest req) {
		System.out.println("sendPoruka()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>(agentEntity.getStatusCode());
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		ResponseEntity<Korisnik> korisnikEntity = restTemplate.getForEntity("https://korisnik-service/korisnik-service//korisnici/"+novaPoruka.getPrimalac(), Korisnik.class);
		if (korisnikEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Korisnik korisnik = korisnikEntity.getBody();
		if (korisnik == null) {			
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		Poruka poruka = new Poruka(null, agent.getIdAgenta(), TipOsobe.AGENT, novaPoruka.getPrimalac(), TipOsobe.KORISNIK, novaPoruka.getSadrzaj(), StatusPoruke.POSLATA);
		if (!Pattern.matches("[\\p{L}\\p{M}]+", poruka.getSadrzaj())) {
			return new ResponseEntity<>(new Valid(false, "PORUKA_CHAR"), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Poruka retVal = porukeService.save(poruka);
		
		return new ResponseEntity<>(new PorukaDTO(retVal), HttpStatus.CREATED);
	}
}
