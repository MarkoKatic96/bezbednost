package com.megatravel.smestajservice.validators;

import java.util.ArrayList;

import com.megatravel.smestajservice.model.TipSmestaja;

public class TipSmestajaValidator implements Validator<TipSmestaja>{

	@Override
	public Valid validate(TipSmestaja t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		if (t.getNazivTipaSmestaja().isEmpty()) {
			return new Valid(false, "NAZIV");
		}
		if (t.getSmestaji()==null) {
			t.setSmestaji(new ArrayList<>());
		}
		return new Valid(true, "");
	}

}
