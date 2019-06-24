package io.webxml.reservationservice.validators;

import io.webxml.reservationservice.model.Rezervacija;

public class RezervacijaValidator implements Validator<Rezervacija>{

	@Override
	public Valid validate(Rezervacija t) {

		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getSmestajId()==null) {
			return new Valid(false, "SMESTAJ_NULL");
		}
		
		if (t.getVlasnikId()==null) {
			return new Valid(false, "VLASNIK_NULL");
		}
		
		if (t.getKorisnikId()==null) {
			return new Valid(false, "KORISNIK_NULL");
		}
		
		if (t.getOdDatuma()==null) {
			return new Valid(false, "OD_DATUM_NULL");
		}
		
		if (t.getDoDatuma()==null) {
			return new Valid(false, "DO_DATUM_NULL");
		}
		
		if (!t.getOdDatuma().before(t.getDoDatuma())) {
			return new Valid(false, "OD_AFTER_DO");
		}
		
		return new Valid(true, "");
	}

}
