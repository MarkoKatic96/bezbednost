package io.webxml.reservationservice.validators;

import io.webxml.reservationservice.model.PotvrdaRezervacije;

public class PotvrdaRezervacijeValidator implements Validator<PotvrdaRezervacije> {

	@Override
	public Valid validate(PotvrdaRezervacije t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getRezervacijaId()==null) {
			return new Valid(false, "REZERVACIJA_NULL");
		}
		
		if (t.getStatusRezervacije()==null) {
			return new Valid(false, "STATUS_NULL");
		}
		
		return new Valid(true, "");
	}

}
