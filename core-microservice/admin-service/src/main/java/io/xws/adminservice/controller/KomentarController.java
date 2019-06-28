package io.xws.adminservice.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.xws.adminservice.model.Komentar;
import io.xws.adminservice.service.KomentarService;


@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/rating-service/admin")
public class KomentarController 
{
	@Autowired
	private KomentarService adminService;
	
	Logger log = LogManager.getLogger(KomentarController.class);
	
	//prikazi sve neobjavljene komentare
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping("/unpublcomms")
	public ResponseEntity<List<Komentar>> getAllNeobjavljeniKomentari()
	{
		System.out.println("getAllNeobjavljeniKomentar()");
		
		List<Komentar> komentari = adminService.getAllNeobjavljeniKomentari();
		
		return (komentari.isEmpty()) ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<List<Komentar>>(komentari, HttpStatus.OK);
	}
	
	
	/*
	 * Objavljivanje komentara (jedan po jedan)
	 * Vraca TRUE ako je uspesno objavljen
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PutMapping("/publcomm/{id}")
	public ResponseEntity<Boolean> updateObjaviKomentar(@PathVariable("id") Long id, HttpServletRequest req)
	{
		System.out.println("updateObjaviKomentar()");
		
		if(!adminService.updateObjaviKomentar(id))
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "updateObjaviKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "updateObjaviKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<Boolean>(false, HttpStatus.METHOD_NOT_ALLOWED);
		}
		else
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "updateObjaviKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "updateObjaviKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<Boolean>(true, HttpStatus.ACCEPTED);
		}
	}
	
	
	/*
	 * Blokiranje komentara (zapravo je brisanje komentara)
	 * Vraca TRUE ako je uspesno blokiran (obrisan)
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@DeleteMapping("/blockcomm/{id}")
	public ResponseEntity<Boolean> blockKomentar(@PathVariable("id") Long id, HttpServletRequest req)
	{
		System.out.println("deleteKomentar()");
		
		if(!adminService.blockKomentar(id))
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blockKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.error("Failed - ProcessID: {} - IPAddress: {} - Type: {}", "blockKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<Boolean>(false, HttpStatus.METHOD_NOT_ALLOWED);
		}
		else
		{
			if(req.getHeader("X-FORWARDED-FOR")==null)
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "blockKomentar", req.getRemoteAddr(), req.getMethod());
			else
				log.info("Success - ProcessID: {} - IPAddress: {} - Type: {}", "blockKomentar", req.getHeader("X-FORWARDED-FOR"), req.getMethod());
			
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		
	}
	
	//?
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
	public Komentar getFilteredSmestaj(@RequestBody Komentar dto){	
		return adminService.createKomentar(dto);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@RequestMapping(value = "/all")
	public List<Komentar> getAll(){
		return adminService.getAllObjavljeniKomentari();
	}
	
	
}
