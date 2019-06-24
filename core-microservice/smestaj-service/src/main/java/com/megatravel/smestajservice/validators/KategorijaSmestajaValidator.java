package com.megatravel.smestajservice.validators;

import java.util.ArrayList;

import com.megatravel.smestajservice.model.KategorijaSmestaja;

public class KategorijaSmestajaValidator implements Validator<KategorijaSmestaja>{

	@Override
	public Valid validate(KategorijaSmestaja t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		if (t.getNaziv().isEmpty()) {
			return new Valid(false, "NAZIV");
		}
		if (t.getSmestaji()==null) {
			t.setSmestaji(new ArrayList<>());
		}
		return new Valid(true, "");
	}

}
