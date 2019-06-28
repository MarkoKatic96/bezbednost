package io.webxml.reservationservice.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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

import io.webxml.reservationservice.dto.LokalneRezervacijeDTO;
import io.webxml.reservationservice.dto.RezervacijaDTO;
import io.webxml.reservationservice.dto.SamostalnaRezervacijaDTO;
import io.webxml.reservationservice.model.Agent;
import io.webxml.reservationservice.model.PotvrdaRezervacije;
import io.webxml.reservationservice.model.Rezervacija;
import io.webxml.reservationservice.model.SamostalnaRezervacija;
import io.webxml.reservationservice.model.StatusRezervacije;
import io.webxml.reservationservice.security.JwtTokenUtils;
import io.webxml.reservationservice.service.RezervacijaService;
import io.webxml.reservationservice.service.SamostalnaRezervacijaService;
import io.webxml.reservationservice.validators.PotvrdaRezervacijeValidator;
import io.webxml.reservationservice.validators.SamostalnaRezervacijaValidator;
import io.webxml.reservationservice.validators.Valid;

@RestController
@RequestMapping("/reservation-service/agent")
public class RezervacijaAgentController {

	@Autowired
	SamostalnaRezervacijaService samostalnaRezervacijaService;
	
	@Autowired
	RezervacijaService rezervacijaService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	
	Logger log = LogManager.getLogger(RezervacijaAgentController.class);
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createRezervacija(@RequestBody SamostalnaRezervacijaDTO rezDTO, HttpServletRequest req) {
		System.out.println("createRezervacija()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezervacija", req.getRemoteAddr(), req.getMethod(), rezDTO.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezDTO.getTimestamp());
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		SamostalnaRezervacija s = new SamostalnaRezervacija(null, rezDTO.getSmestajId(), agent.getIdAgenta(), rezDTO.getOdDatuma(), rezDTO.getDoDatuma());
		
		Valid v = new SamostalnaRezervacijaValidator().validate(s);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezervacija", req.getRemoteAddr(), req.getMethod(), rezDTO.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezDTO.getTimestamp());
			
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		SamostalnaRezervacija retVal = samostalnaRezervacijaService.save(s);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezervacija", req.getRemoteAddr(), req.getMethod(), rezDTO.getTimestamp());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), rezDTO.getTimestamp());
		
		
		return new ResponseEntity<>(new SamostalnaRezervacijaDTO(retVal), HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteRezervacija(@PathVariable Long id, HttpServletRequest req) {
		System.out.println("deleteRezervacija()");
		
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
		
		SamostalnaRezervacija rez = samostalnaRezervacijaService.findOne(id,agent.getIdAgenta());
		if (rez != null) {
			samostalnaRezervacijaService.remove(id);
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "deleteRezervacija", req.getRemoteAddr(), req.getMethod());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "deleteRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "deleteRezervacija", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "deleteRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/potvrdi", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> potvrdiRezervacija(@RequestBody PotvrdaRezervacije potvrda, HttpServletRequest req) {
		System.out.println("potvrdiRezervacija()");
		
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
		
		Valid v = new PotvrdaRezervacijeValidator().validate(potvrda);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getRemoteAddr(), req.getMethod(), potvrda.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), potvrda.getTimestamp());
			
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		if (potvrda.getStatusRezervacije()!=StatusRezervacije.POTVRDJENA && potvrda.getStatusRezervacije()!=StatusRezervacije.NEIZVRSENA) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getRemoteAddr(), req.getMethod(), potvrda.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), potvrda.getTimestamp());
			
			return new ResponseEntity<>(new Valid(false, "STATUS"), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		Rezervacija rezervacija = rezervacijaService.findOne(potvrda.getRezervacijaId(), agent.getIdAgenta());
		if (rezervacija==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getRemoteAddr(), req.getMethod(), potvrda.getTimestamp());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), potvrda.getTimestamp());
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		rezervacija.setStatusRezervacije(potvrda.getStatusRezervacije());
		Rezervacija retVal = rezervacijaService.save(rezervacija);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "potvrdiRezervacija", req.getRemoteAddr(), req.getMethod(), potvrda.getTimestamp());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Time: {}", "createRezpotvrdiRezervacijaervacija", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), potvrda.getTimestamp());
		
		return new ResponseEntity<>(new RezervacijaDTO(retVal), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/update/{timestamp}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<RezervacijaDTO>> getRezervacijeUpdate(@PathVariable String timestamp, HttpServletRequest req) {
		System.out.println("getRezervacijeUpdate()");
		
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
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = format.parse ( timestamp );
		} catch (ParseException e) {
			//e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		} 
		
		List<Rezervacija> rezervacije = rezervacijaService.findAllAfter(date, agent.getIdAgenta());
		List<RezervacijaDTO> retVal = new ArrayList<>();
		
		for (Rezervacija rez : rezervacije) {
			RezervacijaDTO rezDTO = new RezervacijaDTO(rez);
			retVal.add(rezDTO);
		}
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LokalneRezervacijeDTO>> sendRezervacijeUpdate(@RequestBody List<LokalneRezervacijeDTO> listaLokalnihRezervacija, HttpServletRequest req) {
		System.out.println("sendRezervacijeUpdate()");
		
		String token = jwtTokenUtils.resolveToken(req);
		String email = jwtTokenUtils.getUsername(token);
		
		ResponseEntity<Agent> agentEntity = restTemplate.getForEntity("https://agent-global-service/agent/e/"+email, Agent.class);
		if (agentEntity.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Agent agent = agentEntity.getBody();
		if (agent == null) {			
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "sendRezervacijeUpdate", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} ", "sendRezervacijeUpdate", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (listaLokalnihRezervacija==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "sendRezervacijeUpdate", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} ", "sendRezervacijeUpdate", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		ArrayList<LokalneRezervacijeDTO> retVal = new ArrayList<>();
		
		for (LokalneRezervacijeDTO lokalneRezervacijeDTO : listaLokalnihRezervacija) {
			if (rezervacijaService.konfliktRezervacijaExists(agent.getIdAgenta(), lokalneRezervacijeDTO.getSmestajId(), lokalneRezervacijeDTO.getOdDatuma(), lokalneRezervacijeDTO.getDoDatuma())) {
				// postoji rezervacija koja zauzima smestaj u tom periodu
				// ne smem dodati tu rezervaciju u globalnu bazu
				// vracam lokalnoj bazi globalId = null da je obavestim da je rezervacija nevazeca
				
				lokalneRezervacijeDTO.setGlobalniId(null);
				retVal.add(lokalneRezervacijeDTO);
			} else {
				// dodajem rezervaciju u globalnu bazu i vracam lokalnoj bazi id te rezervacije
				
				Rezervacija rez = new Rezervacija(lokalneRezervacijeDTO.getGlobalniId(), lokalneRezervacijeDTO.getSmestajId(), lokalneRezervacijeDTO.getVlasnikId(), lokalneRezervacijeDTO.getKorisnikId(), lokalneRezervacijeDTO.getOdDatuma(), lokalneRezervacijeDTO.getDoDatuma(), lokalneRezervacijeDTO.getStatusRezervacije());
				retVal.add(new LokalneRezervacijeDTO(rezervacijaService.save(rez), lokalneRezervacijeDTO.getLokalniId()));
			}
		}
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "sendRezervacijeUpdate", req.getRemoteAddr(), req.getMethod());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} ", "sendRezervacijeUpdate", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
		
		
		return new ResponseEntity<>(retVal, HttpStatus.OK);
	}
	
}
