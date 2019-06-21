package io.xws.adminservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.xws.adminservice.model.NeaktiviranAgent;
import io.xws.adminservice.service.AgentService;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/agent-global-service/admin")
public class AgentController 
{
	@Autowired
	private AgentService adminService;
	
	
	/*
	 * Vraca sve agente koji su poslali zahtev (cekaju na odobrenje admina)
	 * To znamo na osnovu toga sto im je lozinka prazno polje (ovo ispravi)
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping("/allrequests")
	public ResponseEntity<List<NeaktiviranAgent>> getAllZahteviNeregAgenata()
	{
		System.out.println("getAllZahteviAgenata()");
		
		List<NeaktiviranAgent> agenti = adminService.getAllZahteviNeregAgenata();
		
		return (agenti.isEmpty()) ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<List<NeaktiviranAgent>>(agenti, HttpStatus.OK);
	}
	
	/*
	 * Registrovanje novog agenta tj. dodavanje u sistem
	 * Zbog ispisa poruke na view moze se prebaciti da vraca string, mada treba na agentu da se proveri prvo
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PostMapping("/confirmrequest/{id}")
	public ResponseEntity<String> createPotvrdiZahtev(@PathVariable("id") Long id)
	{
		System.out.println("createPotvrdiZahtev()");
		
		String response = adminService.createPotvrdiZahtev(id);
		
		return (!response.equals("OK")) ? new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST) : new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
		
	}
	
	/*
	 * Odbijanje zahteva za agenta (brisanje zahteva) (prepravi na id)
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@DeleteMapping("/refuserequest/{id}")
	public ResponseEntity<Boolean> deleteOdbijZahtev(@PathVariable("id") Long id)
	{
		System.out.println("deleteOdbijZahtev()");
		
		return (!adminService.deleteOdbijZahtev(id)) ? new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST) : new ResponseEntity<Boolean>(true, HttpStatus.OK);
		
	}

}
