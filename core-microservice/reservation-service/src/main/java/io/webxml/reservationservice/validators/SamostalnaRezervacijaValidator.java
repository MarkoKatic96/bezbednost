package io.webxml.reservationservice.validators;

import io.webxml.reservationservice.model.SamostalnaRezervacija;

public class SamostalnaRezervacijaValidator implements Validator<SamostalnaRezervacija> {

	@Override
	public Valid validate(SamostalnaRezervacija t) {

		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getSmestajId()==null) {
			return new Valid(false, "SMESTAJ_NULL");
		}
		
		if (t.getVlasnikId()==null) {
			return new Valid(false, "VLASNIK_NULL");
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
