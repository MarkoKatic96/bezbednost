package com.megatravel.agentlocalbackend.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.megatravel.agentlocalbackend.configuration.RestTemplateConfiguration;
import com.megatravel.agentlocalbackend.security.JwtTokenUtils;
import com.megatravel.agentlocalbackend.service.AgentService;
import com.megatravel.agentlocalbackend.service.RezervacijaService;
import com.megatravel.agentlocalbackend.soap.AgentClient;
import com.megatravel.agentlocalbackend.validators.AgentRegistracijaDTOValidator;
import com.megatravel.agentlocalbackend.validators.AgentValidator;
import com.megatravel.agentlocalbackend.validators.Valid;
import com.megatravel.agentlocalbackend.wsdl.AgentDTO;
import com.megatravel.agentlocalbackend.wsdl.AgentRegistracijaDTO;
import com.megatravel.agentlocalbackend.wsdl.EditResponse;
import com.megatravel.agentlocalbackend.wsdl.GetAgentByEmailResponse;
import com.megatravel.agentlocalbackend.wsdl.GetAgentByEmailResponse.Agent;
import com.megatravel.agentlocalbackend.wsdl.GetAgentResponse;
import com.megatravel.agentlocalbackend.wsdl.SignUpResponse;
import com.megatravel.agentlocalbackend.wsdl.SignUpResponse.NeaktiviranAgent;


@RestController
@RequestMapping("/agent-local-service/agent")
public class AgentController {
	
	@Autowired
	RestTemplateConfiguration config;
	
	@Autowired
	RezervacijaService rezervacijaService;
	
	@Autowired
	AgentService agentService;
	
	@Autowired
	AgentClient agentClient;
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	@Autowired
	RestTemplate restTemplate;
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<AgentDTO> getAgent(@PathVariable Long id) {
		System.out.println("getAgent(" + id + ")");
		
		com.megatravel.agentlocalbackend.model.Agent agent = agentService.findOne(id);
		if (agent == null) {
			GetAgentResponse agentResponse = agentClient.getAgent(id);
			AgentDTO agentDTO = agentResponse.getAgent();
			return new ResponseEntity<>(agentDTO, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(new AgentDTO(), HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/e/{email}", method = RequestMethod.GET)
	public ResponseEntity<com.megatravel.agentlocalbackend.model.Agent> getAgentByEmail(@PathVariable String email, HttpServletRequest req) {
		System.out.println("getAgentByEmail(" + email + ")");
		
		com.megatravel.agentlocalbackend.model.Agent agent = agentService.findByEmail(email);
		if (agent == null) {
			GetAgentByEmailResponse agentByEmailResponse = agentClient.getAgentByEmail(email);
			com.megatravel.agentlocalbackend.wsdl.GetAgentByEmailResponse.Agent agentNovi = agentByEmailResponse.getAgent(); 
			
			if (agentNovi==null) {
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			} else {
				String token = jwtTokenUtils.resolveToken(req);
				String agentEmail = jwtTokenUtils.getUsername(token);
				
				ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("http://agent-global-service/agent/e/"+agentEmail, Agent.class);
				if (agentEntity.getStatusCode() != HttpStatus.OK) {
					return new ResponseEntity<>(agentEntity.getStatusCode());
				}
				
				agent = new com.megatravel.agentlocalbackend.model.Agent(agentNovi.getIdAgenta(), agentNovi.getIme(), agentNovi.getPrezime(), agentNovi.getPoslovniMaticniBroj(), agentNovi.getEmail(), agentNovi.getLozinka());
				agent.setDatumClanstva(agentNovi.getDatumClanstva());
				agentService.deleteAll();
				agentService.save(agent);
			}
			
		}

		return new ResponseEntity<>(agent, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/edit", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> edit(@RequestBody Agent noviAgent, HttpServletRequest req) {
		System.out.println("edit(" + noviAgent.getEmail() + "," + noviAgent.getLozinka() + ")");
		
		EditResponse editResponse = agentClient.getEdit(noviAgent);
		AgentDTO agentDTO = editResponse.getAgent();
		if (agentDTO==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		} else {
			String token = jwtTokenUtils.resolveToken(req);
			String agentEmail = jwtTokenUtils.getUsername(token);
			
			ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("http://agent-global-service/agent/e/"+agentEmail, Agent.class);
			if (agentEntity.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>(agentEntity.getStatusCode());
			}
			
			Valid v = new AgentValidator().validate(noviAgent);
			if (!v.isValid()) {
				return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
			}
			
			com.megatravel.agentlocalbackend.model.Agent a = new com.megatravel.agentlocalbackend.model.Agent();
			a.setDatumClanstva(agentDTO.getDatumClanstva());
			a.setEmail(agentDTO.getEmail());
			a.setIdAgenta(agentDTO.getIdAgenta());
			a.setIme(agentDTO.getIme());
			a.setLozinka(noviAgent.getLozinka());
			a.setPoslovniMaticniBroj(agentDTO.getPoslovniMaticniBroj());
			a.setPrezime(agentDTO.getPrezime());
			
			agentService.save(a);
			return new ResponseEntity<>(agentDTO, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> signup(@RequestBody AgentRegistracijaDTO agentRegistracijaDTO) {
		System.out.println("signup()");
		/*
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
		*/
		
		Valid v = new AgentRegistracijaDTOValidator().validate(agentRegistracijaDTO);
		if (!v.isValid()) {
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		SignUpResponse signupResponse = agentClient.getSignUp(agentRegistracijaDTO);
		NeaktiviranAgent agent = signupResponse.getNeaktiviranAgent();
		
		try{
			if (agent.getIdNeaktiviranogAgenta()==0 || agent==null) {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<>(agent, HttpStatus.CREATED);
	}
	
	/*@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public ResponseEntity<Void> signout(HttpServletRequest request) {
		System.out.println("signout()");
		
		String signOutUrl = "https://localhost:8400/agent/signout";
		
		RestTemplate restTemplate = config.createRestTemplate();
	    try {
	    	String body = IOUtils.toString(request.getInputStream(), Charset.forName(request.getCharacterEncoding()));
	        ResponseEntity<Void> exchange = restTemplate.exchange(signOutUrl,
	                HttpMethod.valueOf(request.getMethod()),
	                new HttpEntity<>(body),
	                Void.class,
	                request.getParameterMap());
	        
	        rezervacijaService.deleteAll();
			agentService.deleteAll();
			
	        return exchange;
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}*/
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<Boolean> validateToken() {
		System.out.println("validateToken()");
	
		return new ResponseEntity<>(new Boolean(true), HttpStatus.OK);
	}
	
}
