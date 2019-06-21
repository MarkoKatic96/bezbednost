package com.megatravel.ratingservice.dto;

public class NoviKomentarDTO {

	private Long idSmestaja;
    private Long idRezervacije;
    private Long idKorisnika;
    private String komentar;
	  
    public NoviKomentarDTO() {	}

	public Long getIdSmestaja() {
		return idSmestaja;
	}

	public void setIdSmestaja(Long idSmestaja) {
		this.idSmestaja = idSmestaja;
	}

	public Long getIdRezervacije() {
		return idRezervacije;
	}

	public void setIdRezervacije(Long idRezervacije) {
		this.idRezervacije = idRezervacije;
	}

	public Long getIdKorisnika() {
		return idKorisnika;
	}

	public void setIdKorisnika(Long idKorisnika) {
		this.idKorisnika = idKorisnika;
	}

	public String getKomentar() {
		return komentar;
	}

	public void setKomentar(String komentar) {
		this.komentar = komentar;
	}    
}
