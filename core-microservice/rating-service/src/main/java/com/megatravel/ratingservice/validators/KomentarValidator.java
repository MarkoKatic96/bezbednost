package com.megatravel.ratingservice.validators;

import java.util.regex.Pattern;

import com.megatravel.ratingservice.model.Komentar;

public class KomentarValidator implements Validator<Komentar>{

	@Override
	public Valid validate(Komentar t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getIdSmestaja()==null) {
			return new Valid(false, "SMESTAJ_NULL");
		}
		
		if (t.getIdRezervacije()==null) {
			return new Valid(false, "REZERVACIJA_NULL");
		}
		
		if (t.getIdKorisnika()==null) {
			return new Valid(false, "KORISNIK_NULL");
		}
		
		if (!Pattern.matches("[\\p{L}\\p{M}]+", t.getKomentar())) {
			return new Valid(false, "KOMENTAR_CHAR");
		}

		if (t.getTimestamp()==null) {
			return new Valid(false, "TIMESTAMP_NULL");
		}
		
		if (t.getStatus()==null) {
			return new Valid(false, "STATUS_NULL");
		}
		
		return new Valid(true, "");
	}

}
