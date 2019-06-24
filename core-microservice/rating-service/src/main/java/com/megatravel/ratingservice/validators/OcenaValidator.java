package com.megatravel.ratingservice.validators;

import com.megatravel.ratingservice.model.Ocena;

public class OcenaValidator implements Validator<Ocena>{

	@Override
	public Valid validate(Ocena t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getIdSmestaj()==null) {
			return new Valid(false, "SMESTAJ_NULL");
		}
		
		if (t.getIdRezervacija()==null) {
			return new Valid(false, "REZERVACIJA_NULL");
		}
		
		if (t.getIdKorisnik()==null) {
			return new Valid(false, "KORISNIK_NULL");
		}
		
		if (t.getOcena()<0 || t.getOcena()>5) {
			return new Valid(false, "OCENA_RANGE");
		}
		
		return new Valid(true, "");
	}

}
