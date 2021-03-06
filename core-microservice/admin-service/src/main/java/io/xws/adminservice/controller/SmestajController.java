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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.xws.adminservice.model.DodatneUsluge;
import io.xws.adminservice.model.KategorijaSmestaja;
import io.xws.adminservice.model.TipSmestaja;
import io.xws.adminservice.service.SmestajRestService;
import io.xws.adminservice.service.SmestajService;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/smestaj-service/adminrest")
public class SmestajController
{
	@Autowired
	private SmestajService smestajService;
	
	@Autowired
	private SmestajRestService adminService;
	
	Logger log = LogManager.getLogger(SmestajController.class);
	
	
	  //////////////////////////////////////////////
	 ////////OPERACIJE SA TIPOVIMA SMESTAJA////////
	//////////////////////////////////////////////
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping("/gettypes")
	public ResponseEntity<List<TipSmestaja>> getAllTipoviSmestaja()
	{
		System.out.println("getAllTipoviSmestaja()");
		
		List<TipSmestaja> list = smestajService.getAllTipove();
		
		return (list.isEmpty()) ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<List<TipSmestaja>>(list, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PostMapping("/addtype/")
	public ResponseEntity<TipSmestaja> createTipSmestaja(@RequestBody TipSmestaja dto)
	{
		System.out.println("createTipSmestaja()");
		
		TipSmestaja created = adminService.createTipSmestaja(dto);
		
		return (created == null) ? new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED) : new ResponseEntity<TipSmestaja>(created, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PutMapping("/updatetype/{id}")
	public ResponseEntity<TipSmestaja> updateTipSmestaja(@PathVariable("id") Long id, @RequestBody TipSmestaja updateDto)
	{
		System.out.println("updateTipSmestaja()");
		
		TipSmestaja dto = adminService.updateTipSmestaja(id, updateDto);
		
		return (dto == null) ? new ResponseEntity<>(null, HttpStatus.BAD_REQUEST) : new ResponseEntity<TipSmestaja>(dto, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@DeleteMapping("/deletetype/{id}")
	public ResponseEntity<Boolean> deleteTipSmestaja(@PathVariable("id") Long idToDelete)
	{
		System.out.println("deleteTipSmestaja()");
		
		return (!adminService.deleteTipSmestaja(idToDelete)) ? new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST) : new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	
	
	 //////////////////////////////////////////////
	///////OPERACIJE SA KATEGORIJOM SMESTAJA//////
   //////////////////////////////////////////////
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping("/getcategories")
	public ResponseEntity<List<KategorijaSmestaja>> getAllKategorijeSmestaja()
	{
		System.out.println("getAllKategorijeSmestaja()");
		
		List<KategorijaSmestaja> list = smestajService.getAllKategorije();
		
		return (list.isEmpty()) ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<List<KategorijaSmestaja>>(list, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PostMapping("/addcategory/")
	public ResponseEntity<KategorijaSmestaja> createKategorijaSmestaja(@RequestBody KategorijaSmestaja dto)
	{
		System.out.println("createKategorijaSmestaja()");
		
		KategorijaSmestaja created = adminService.createKategorijaSmestaja(dto);
		
		return (created == null) ? new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED) : new ResponseEntity<KategorijaSmestaja>(created, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PutMapping("/updatecategory/{id}")
	public ResponseEntity<KategorijaSmestaja> updateKategorijaSmestaja(@PathVariable("id") Long id, @RequestBody KategorijaSmestaja updateDto)
	{
		System.out.println("updateKategorijaSmestaja()");
		
		KategorijaSmestaja dto = adminService.updateKategorijaSmestaja(id, updateDto);
		
		return (dto == null) ? new ResponseEntity<>(null, HttpStatus.BAD_REQUEST) : new ResponseEntity<KategorijaSmestaja>(dto, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@DeleteMapping("/deletecategory/{id}")
	public ResponseEntity<Boolean> deleteKategorijaSmestaja(@PathVariable("id") Long idToDelete)
	{
		System.out.println("deleteKategorijaSmestaja()");
		
		return (!adminService.deleteKategorijaSmestaja(idToDelete)) ? new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST) : new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	
	
	 //////////////////////////////////////////////
	////////OPERACIJE SA DODATNIM USLUGAMA////////
   //////////////////////////////////////////////
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping("/getservices")
	public ResponseEntity<List<DodatneUsluge>> getAllDodatneUsluge()
	{
		System.out.println("getAllDodatneUsluge()");
		
		List<DodatneUsluge> list = smestajService.getAllUsluge();
		
		return (list.isEmpty()) ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<List<DodatneUsluge>>(list, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PostMapping("/addservice/")
	public ResponseEntity<DodatneUsluge> createDodatnaUsluga(@RequestBody DodatneUsluge dto)
	{
		System.out.println("createDodatnaUsluga()");
		
		DodatneUsluge created = adminService.createDodatnaUsluga(dto);
		
		return (created == null) ? new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED) : new ResponseEntity<DodatneUsluge>(created, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PutMapping("/updateservice/{id}")
	public ResponseEntity<DodatneUsluge> updateDodatnaUsluga(@PathVariable("id") Long id, @RequestBody DodatneUsluge updateDto)
	{
		System.out.println("updateDodatnaUsluga()");
		
		DodatneUsluge dto = adminService.updateDodatnaUsluga(id, updateDto);
		
		return (dto == null) ? new ResponseEntity<>(null, HttpStatus.BAD_REQUEST) : new ResponseEntity<DodatneUsluge>(dto, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@DeleteMapping("/deleteservice/{id}")
	public ResponseEntity<Boolean> deleteDodatnaUsluga(@PathVariable("id") Long idToDelete)
	{
		System.out.println("deleteDodatnaUsluga()");
		
		return (!adminService.deleteDodatnaUsluga(idToDelete)) ? new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST) : new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}
