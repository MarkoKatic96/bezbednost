package com.megatravel.smestajservice.validators;

import java.math.BigDecimal;

import com.megatravel.smestajservice.model.TKoordinate;

public class TKoordinateValidator implements Validator<TKoordinate>{

	@Override
	public Valid validate(TKoordinate t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		if (t.getSmestaj()==null) {
			return new Valid(false, "SMESTAJ_NULL");
		}
		if (t.getLatitude().compareTo(new BigDecimal(-90))<0) {
			return new Valid(false, "LAT_MIN");
		}
		if (t.getLatitude().compareTo(new BigDecimal(90))>0) {
			return new Valid(false, "LAT_MAX");
		}
		if (t.getLongitude().compareTo(new BigDecimal(-180))<0) {
			return new Valid(false, "LON_MIN");
		}
		if (t.getLongitude().compareTo(new BigDecimal(180))>0) {
			return new Valid(false, "LON_MAX");
		}
		return new Valid(true, "");
	}

}
