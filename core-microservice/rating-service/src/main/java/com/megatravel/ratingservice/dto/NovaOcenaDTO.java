package com.megatravel.ratingservice.dto;

public class NovaOcenaDTO {
	
    private Long idSmestaj;
    private Long idRezervacija;
    private Long idKorisnik;
    private short ocena;

    public NovaOcenaDTO() { }
	
    /**
     * Gets the value of the idSmestaj property.
     * 
     */
    public Long getIdSmestaj() {
        return idSmestaj;
    }

    /**
     * Gets the value of the idRezervacija property.
     * 
     */
    public Long getIdRezervacija() {
        return idRezervacija;
    }

    /**
     * Gets the value of the idKorisnik property.
     * 
     */
    public Long getIdKorisnik() {
        return idKorisnik;
    }

    /**
     * Gets the value of the ocena property.
     * 
     */
    public short getOcena() {
        return ocena;
    }

    /**
     * Sets the value of the ocena property.
     * 
     */
    public void setOcena(short value) {
        this.ocena = value;
    }

}
