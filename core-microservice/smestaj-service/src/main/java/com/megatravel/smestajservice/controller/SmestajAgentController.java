package com.megatravel.smestajservice.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.megatravel.smestajservice.dto.SmestajDTO;
import com.megatravel.smestajservice.model.Agent;
import com.megatravel.smestajservice.model.Smestaj;
import com.megatravel.smestajservice.security.JwtTokenUtils;
import com.megatravel.smestajservice.service.SmestajService;
import com.megatravel.smestajservice.validators.SmestajValidator;
import com.megatravel.smestajservice.validators.Valid;

@RestController
@RequestMapping("/smestaj-service/smestaj")
public class SmestajAgentController {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	SmestajService smestajService;
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	Logger log = LogManager.getLogger(SmestajAgentController.class);
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<List<SmestajDTO>> getAllSmestaji(HttpServletRequest req, Pageable page) {
		System.out.println("getAllSmestaj()");
		
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
		Page<Smestaj> smestaji = smestajService.getAll(agent.getIdAgenta(), page);
		
		HttpHeaders headers = new HttpHeaders();
		long korisniciTotal = smestaji.getTotalElements();
		headers.add("X-Total-Count", String.valueOf(korisniciTotal));

		List<SmestajDTO> retVal = new ArrayList<SmestajDTO>();

		for (Smestaj s : smestaji) {
			SmestajDTO smestajDTO = new SmestajDTO(s);
			retVal.add(smestajDTO);
		}

		return new ResponseEntity<>(retVal, headers, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<SmestajDTO> getSmestaj(@PathVariable Long id, HttpServletRequest req) {
		System.out.println("getSmestaj()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		Smestaj smestaj = smestajService.findOne(id, agent.getIdAgenta());
		
		if (smestaj==null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new SmestajDTO(smestaj), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@RequestBody SmestajDTO smestajDTO, HttpServletRequest req) {
		System.out.println("create()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "create", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "create", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
			
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Smestaj s = new Smestaj();
		s.setAdresa(smestajDTO.getAdresa());
		s.setLatitude(smestajDTO.getLatitude());
		s.setLongitude(smestajDTO.getLongitude());
		s.setTipSmestaja(smestajDTO.getTipSmestaja());
		s.setKategorijaSmestaja(smestajDTO.getKategorijaSmestaja());
		s.setOpis(smestajDTO.getOpis());
		s.setMaxOsoba(smestajDTO.getMaxOsoba());
		s.setMaxDanaZaOtkazivanje(smestajDTO.getMaxDanaZaOtkazivanje());
		s.setCenaProlece(smestajDTO.getCenaProlece());
		s.setCenaLeto(smestajDTO.getCenaLeto());
		s.setCenaJesen(smestajDTO.getCenaJesen());
		s.setCenaZima(smestajDTO.getCenaZima());
		s.setVlasnik(agent.getIdAgenta());
		s.setListaDodatnihUsluga(smestajDTO.getListaDodatnihUsluga());
		s.setListaSlika(smestajDTO.getListaSlika());

		Valid v = new SmestajValidator().validate(s);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "create", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "create", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
			
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Smestaj retVal = smestajService.save(s);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "create", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "create", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
		

		return new ResponseEntity<>(new SmestajDTO(retVal), HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SmestajDTO smestajDTO, HttpServletRequest req) {
		System.out.println("update()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
			
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Smestaj smestaj = smestajService.findOne(id, agent.getIdAgenta());
		if (smestaj == null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
			
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		smestaj.update(smestajDTO);
		
		Valid v = new SmestajValidator().validate(smestaj);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
			
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}

		Smestaj retVal = smestajService.save(smestaj);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getRemoteAddr(), req.getMethod(), smestajDTO.getAdresa());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Address: {}", "update", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), smestajDTO.getAdresa());
		

		return new ResponseEntity<>(new SmestajDTO(retVal), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
		System.out.println("delete()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {		
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "delete", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "delete", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Smestaj smestaj = smestajService.findOne(id,agent.getIdAgenta());
		if (smestaj != null) {
			smestajService.remove(id);
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "delete", req.getRemoteAddr(), req.getMethod());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "delete", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(HttpStatus.OK);

		} else {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "delete", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "delete", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
