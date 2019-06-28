package com.megatravel.ratingservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.megatravel.ratingservice.dto.NovaOcenaDTO;
import com.megatravel.ratingservice.model.Ocena;
import com.megatravel.ratingservice.service.OcenaService;
import com.megatravel.ratingservice.validators.OcenaValidator;
import com.megatravel.ratingservice.validators.Valid;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/rating-service/ocena")
public class OcenaController {
	
	@Autowired
	OcenaService ocenaService;
	
	Logger log = LogManager.getLogger(KomentarController.class);

	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createOcena(@RequestBody NovaOcenaDTO novaOcena, HttpServletRequest req){
		
		Ocena ocena = new Ocena(null, novaOcena.getIdSmestaj(), novaOcena.getIdRezervacija(), novaOcena.getIdKorisnik(), novaOcena.getOcena());
		Valid v = new OcenaValidator().validate(ocena);
		if (!v.isValid() || ocena.getOcena()<1) 
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "createOcena", req.getRemoteAddr(), req.getMethod(), novaOcena.getOcena());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "createOcena", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaOcena.getOcena());
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		ocena = ocenaService.save(ocena);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "createOcena", req.getRemoteAddr(), req.getMethod(), novaOcena.getOcena());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "createOcena", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaOcena.getOcena());
		
		
		return new ResponseEntity<>(ocena, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_KORISNIK')")
	@RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> editDeleteOcena(@RequestBody Ocena novaOcena, HttpServletRequest req){
		
		Valid v = new OcenaValidator().validate(novaOcena);
		if (!v.isValid()) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getRemoteAddr(), req.getMethod(), novaOcena.getOcena());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaOcena.getOcena());
			
			
			return new ResponseEntity<>(v.getErrCode(),HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		if (novaOcena.getOcena()==0) {
			//brisanje ocene
			if (ocenaService.remove(novaOcena)) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				
				if(req.getHeader("X-FORWARDED-FOR")==null)
					log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getRemoteAddr(), req.getMethod(), novaOcena.getOcena());
				else
					log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaOcena.getOcena());
				
				
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
			
		//editovanje ocene
		Ocena ocena = ocenaService.findOne(novaOcena.getIdKorisnik());
		if (ocena==null) {
			
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getRemoteAddr(), req.getMethod(), novaOcena.getOcena());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaOcena.getOcena());
			
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		ocena.setOcena(novaOcena.getOcena());
		ocena = ocenaService.save(ocena);
		
		if(req.getHeader("X-FORWARDED-FOR")==null)
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getRemoteAddr(), req.getMethod(), novaOcena.getOcena());
		else
			log.info("Success - ProcessID: {} - IPAddress: {} - Type: {} - Rating: {}", "editDeleteOcena", req.getHeader("X-FORWARDED-FOR"), req.getMethod(), novaOcena.getOcena());
		
		
		return new ResponseEntity<>(ocena, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Float> getAverageOcenaForSmestaj(@PathVariable Long id) {
		float ocena = ocenaService.getAverageOcenaForSmestaj(id);
		if (ocena>0 && ocena<6) {
			return new ResponseEntity<>(new Float(ocena), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
}
