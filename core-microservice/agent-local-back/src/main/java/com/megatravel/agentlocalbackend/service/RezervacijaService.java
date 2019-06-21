package com.megatravel.agentlocalbackend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.megatravel.agentlocalbackend.dto.RezervacijaDTO;
import com.megatravel.agentlocalbackend.model.Rezervacija;
import com.megatravel.agentlocalbackend.repository.RezervacijaRepository;

@Component
public class RezervacijaService {
	
	@Autowired
	private RezervacijaRepository rezervacijaRepository;
	
	public void saveAll(List<RezervacijaDTO> rezervacije) {
		List<Rezervacija> list = new ArrayList<>();
		for (RezervacijaDTO rDTO : rezervacije) {
			list.add(new Rezervacija(rDTO.getRezervacijaId(), rDTO.getSmestajId(), rDTO.getVlasnikId(),
					rDTO.getKorisnikId(), rDTO.getOdDatuma(), rDTO.getDoDatuma(), rDTO.getStatusRezervacije()));
		}
		
		for (Rezervacija r : list) {
			rezervacijaRepository.save(r);
		}
	}
	
	public Rezervacija save(Rezervacija r) {
		return rezervacijaRepository.save(r);
	}
	
	public Rezervacija findOne(Long id) {
		return rezervacijaRepository.getOne(id);
	}
	
	public Date findOldestDate() {
		return rezervacijaRepository.findOldestDate();
	}
	
	public void deleteAll() {
		rezervacijaRepository.deleteAll();
	}
}
