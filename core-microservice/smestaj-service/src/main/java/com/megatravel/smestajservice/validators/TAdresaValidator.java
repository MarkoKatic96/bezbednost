package com.megatravel.smestajservice.validators;

import com.megatravel.smestajservice.model.TAdresa;

public class TAdresaValidator implements Validator<TAdresa> {

	@Override
	public Valid validate(TAdresa t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		if (t.getSmestaj()==null) {
			return new Valid(false, "SMESTAJ_NULL");
		}
		if (t.getGrad().isEmpty()) {
			return new Valid(false, "GRAD_NULL");
		}
		if (t.getUlica().isEmpty()) {
			return new Valid(false, "ULICA_NULL");
		}
		if (t.getBroj()<=0) {
			return new Valid(false, "BROJ_NULL");
		}
		return new Valid(true, "");
	}

}
