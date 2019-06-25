package io.webxml.korisnikservice.dto;

import io.webxml.korisnikservice.model.Korisnik;

public class KorisnikDTO {
	private Long id;
	private String ime;
	private String prezime;
	
	public KorisnikDTO(Long id, String ime, String prezime) {
		super();
		this.id = id;
		this.ime = ime;
		this.prezime = prezime;
	}
	
	public KorisnikDTO(Korisnik korisnik) {
		super();
		this.id = korisnik.getIdKorisnik();
		this.ime = korisnik.getIme();
		this.prezime = korisnik.getPrezime();
	}

	public Long getId() {
		return id;
	}

	public String getIme() {
		return ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}
}
