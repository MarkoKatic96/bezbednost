package io.xws.adminservice.dto;

import java.util.Date;

import io.xws.adminservice.model.Korisnik;

public class KorisnikDTO {
	
    private long idKorisnik;
    private String email;
    private String ime;
    private String prezime;
    private Date datumClanstva;
    private boolean registrovan;
	private boolean blokiran;
    private String rola;
	
    public KorisnikDTO(long idKorisnik, String email, String ime, String prezime, String lozinka, Date datumClanstva,
			boolean registrovan, boolean blokiran, String rola) {
		this.idKorisnik = idKorisnik;
		this.email = email;
		this.ime = ime;
		this.prezime = prezime;
		this.datumClanstva = datumClanstva;
		this.registrovan = registrovan;
		this.blokiran = blokiran;
		this.rola = rola;
	}
    
    public KorisnikDTO(Korisnik k) {
    	this.idKorisnik = k.getIdKorisnik();
		this.email = k.getEmail();
		this.ime = k.getIme();
		this.prezime = k.getPrezime();
		this.datumClanstva = k.getDatumClanstva();
		this.registrovan = k.isRegistrovan();
		this.blokiran = k.isBlokiran();
		this.rola = k.getRola();
    }

	public long getIdKorisnik() {
		return idKorisnik;
	}

	public String getEmail() {
		return email;
	}

	public String getIme() {
		return ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public Date getDatumClanstva() {
		return datumClanstva;
	}

	public boolean isRegistrovan() {
		return registrovan;
	}

	public boolean isBlokiran() {
		return blokiran;
	}

	public String getRola() {
		return rola;
	}

	public void setIdKorisnik(long idKorisnik) {
		this.idKorisnik = idKorisnik;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public void setDatumClanstva(Date datumClanstva) {
		this.datumClanstva = datumClanstva;
	}

	public void setRegistrovan(boolean registrovan) {
		this.registrovan = registrovan;
	}

	public void setBlokiran(boolean blokiran) {
		this.blokiran = blokiran;
	}

	public void setRola(String rola) {
		this.rola = rola;
	}
}
