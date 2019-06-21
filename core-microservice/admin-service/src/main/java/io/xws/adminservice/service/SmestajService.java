package io.xws.adminservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.xws.adminservice.model.DodatneUsluge;
import io.xws.adminservice.model.KategorijaSmestaja;
import io.xws.adminservice.model.Smestaj;
import io.xws.adminservice.model.TipSmestaja;
import io.xws.adminservice.repository.DodatnaUslugaRepository;
import io.xws.adminservice.repository.KategorijaSmestajaRepository;
import io.xws.adminservice.repository.SmestajRepository;
import io.xws.adminservice.repository.TipSmestajaRepository;

@Component
public class SmestajService {

	@Autowired
	private SmestajRepository smestajRepository;
	
	@Autowired
	private DodatnaUslugaRepository dodatnaUslugaRepository;
	
	@Autowired
	private TipSmestajaRepository tipSmestajaRepository;
	
	@Autowired
	private KategorijaSmestajaRepository kategorijaSmestajaRepository;
	
	public List<Smestaj> getSmestaji(){
		List<Smestaj> lista = smestajRepository.pronadjiSve();
		if(lista.isEmpty()) {
			return new ArrayList<Smestaj>();
		}
		return lista;
	} 
	
	public List<TipSmestaja> getAllTipove() {
		return tipSmestajaRepository.getAll();
	}
	
	public List<KategorijaSmestaja> getAllKategorije() {
		return kategorijaSmestajaRepository.getAll();
	}
	
	public List<DodatneUsluge> getAllUsluge(){
		return dodatnaUslugaRepository.getAll();
	}
}
