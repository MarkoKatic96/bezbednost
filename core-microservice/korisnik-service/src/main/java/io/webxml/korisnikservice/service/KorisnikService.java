package io.webxml.korisnikservice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.webxml.korisnikservice.model.Korisnik;
import io.webxml.korisnikservice.repository.KorisnikRepository;

@Service
public class KorisnikService {
	
	@Autowired
	private KorisnikRepository korisnikRepository;

	public List<Korisnik> getAllKorisnici(){
		List<Korisnik> returnList = korisnikRepository.findAllKorisnici();
		return returnList;
	}
	
	public Korisnik getKorisnikById(Long id) {
		Optional<Korisnik> k = korisnikRepository.findById(id);
		if(k.isPresent()) {
			return k.get();
		}
		return null;
	}
	
	public Korisnik getKorisnikByEmail(String email) {
		Korisnik k = korisnikRepository.nadjiKorisnikaPoEmail(email);
		return k;
	}
	
	public Korisnik register(Korisnik korisnik) {
		Korisnik k = korisnikRepository.nadjiKorisnikaPoEmail(korisnik.getEmail());
		if(k==null) {
			korisnik.setRola("KORISNIK");
			korisnik.setDatumClanstva(new Date());
			korisnik.setBlokiran(false);
			korisnik.setRegistrovan(false);
			return korisnikRepository.save(korisnik);
		}
		return null;
	}
	
	/*public String signin(String email, String lozinka) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, lozinka));
			return jwtTokenProvider.createToken(email);
		} catch (AuthenticationException e) {
			throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}*/
	
}
