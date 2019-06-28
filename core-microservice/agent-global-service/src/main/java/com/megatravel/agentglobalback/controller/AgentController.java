package com.megatravel.agentglobalback.controller;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.megatravel.agentglobalback.dto.AgentDTO;
import com.megatravel.agentglobalback.dto.AgentRegistracijaDTO;
import com.megatravel.agentglobalback.model.Agent;
import com.megatravel.agentglobalback.model.NeaktiviranAgent;
import com.megatravel.agentglobalback.security.JwtTokenUtils;
import com.megatravel.agentglobalback.service.AgentService;
import com.megatravel.agentglobalback.service.NeaktiviranAgentService;
import com.megatravel.agentglobalbackend.validators.AgentValidator;
import com.megatravel.agentglobalbackend.validators.Valid;


@RestController
@RequestMapping("/agent-global-service/agent")
public class AgentController {
	
	@Autowired
	NeaktiviranAgentService neaktiviranAgentService;

	@Autowired
	AgentService agentService;
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	Logger log = LogManager.getLogger(AgentController.class);

	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<AgentDTO> getAgent(@PathVariable Long id) {
		System.out.println("getAgent(" + id + ")");
		
		Agent agent = agentService.findOne(id);
		if (agent == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new AgentDTO(agent), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/e/{email}", method = RequestMethod.GET)
	public ResponseEntity<?> getAgentByEmail(@PathVariable String email) {
		System.out.println("getAgentByEmail(" + email + ")");
		
		if (!Pattern.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@megatravel\\.com$", email.trim())) {
			return new ResponseEntity<>(new Valid(false, "EMAIL_CHAR"), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Agent agent = agentService.findByEmail(email);
		if (agent == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(agent, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/edit", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> edit(@RequestBody Agent noviAgent, HttpServletRequest req) {
		System.out.println("edit(" + noviAgent.getEmail() + "," + noviAgent.getLozinka() + ")");
		
		Agent agent = agentService.findByEmail(noviAgent.getEmail());
		if(agent == null) {
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Username: {}", "edit", req.getRemoteAddr(), req.getMethod(), noviAgent.getIme());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Username: {}", "edit", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), noviAgent.getIme());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Valid v = new AgentValidator().validate(noviAgent);
		if (!v.isValid()) {
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Username: {}", "edit", req.getRemoteAddr(), req.getMethod(), noviAgent.getIme());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Username: {}", "edit", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), noviAgent.getIme());
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}

		agent.setLozinka(passwordEncoder.encode(agent.getLozinka()));
		agent = agentService.save(noviAgent);
		return new ResponseEntity<AgentDTO>(new AgentDTO(agent), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<NeaktiviranAgent> signup(@RequestBody AgentRegistracijaDTO agentRegistracijaDTO, HttpServletRequest req) {
		System.out.println("signup()");

		Agent tempKorisnik = agentService.findByEmail(agentRegistracijaDTO.getEmail());
		if(tempKorisnik != null) {
			//mora biti jedinstveni mail za korisnika
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		tempKorisnik = agentService.findByPMB(agentRegistracijaDTO.getPoslovniMaticniBroj());
		if(tempKorisnik != null) {
			//mora biti jedinstveni PIM za korisnika
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		NeaktiviranAgent tempKorisnik2 = neaktiviranAgentService.findByEmail(agentRegistracijaDTO.getEmail());
		if(tempKorisnik2 != null) {
			//mora biti jedinstveni mail za korisnika
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		tempKorisnik2 = neaktiviranAgentService.findByPMB(agentRegistracijaDTO.getPoslovniMaticniBroj());
		if(tempKorisnik2 != null) {
			//mora biti jedinstveni PIM za korisnika
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		NeaktiviranAgent agent = new NeaktiviranAgent(null, agentRegistracijaDTO.getIme(), agentRegistracijaDTO.getPrezime(), agentRegistracijaDTO.getPoslovniMaticniBroj(), agentRegistracijaDTO.getEmail());
		NeaktiviranAgent retValue = neaktiviranAgentService.save(agent);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Username: {}", "signup", req.getRemoteAddr(), req.getMethod(), agentRegistracijaDTO.getIme());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Username: {}", "signup", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), agentRegistracijaDTO.getIme());

		return new ResponseEntity<>(retValue, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<Boolean> validateToken(@RequestBody String token, HttpServletRequest req) {
		System.out.println("validateToken()");
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "validateToken", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "validateToken", req.getHeader("X-FORWARDED-FOR"), req.getMethod());

		
		return new ResponseEntity<>(new Boolean(true), HttpStatus.OK);
	}	
}
