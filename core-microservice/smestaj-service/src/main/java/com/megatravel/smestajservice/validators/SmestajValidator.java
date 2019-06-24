package com.megatravel.smestajservice.validators;

import java.util.regex.Pattern;

import com.megatravel.smestajservice.model.Smestaj;

public final class SmestajValidator implements Validator<Smestaj> {

	@Override
	public Valid validate(Smestaj t) {
		Valid v = null;
		
		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		v = new TAdresaValidator().validate(t.getAdresa());
		if (!v.isValid()) {
			return v;
		}
		
		v = new TKoordinateValidator().validate(t.getKoordinate());
		if (!v.isValid()) {
			return v;
		}
		
		v = new TipSmestajaValidator().validate(t.getTipSmestaja());
		if (!v.isValid()) {
			return v;
		}
		
		v = new KategorijaSmestajaValidator().validate(t.getKategorijaSmestaja());
		if (!v.isValid()) {
			return v;
		}
		
		if (!Pattern.matches("[\\p{L}\\p{M}]+", t.getOpis())) {
			return new Valid(false, "OPIS_CHAR");
		}
		
		if (t.getMaxOsoba()<1) {
			return new Valid(false, "MAX_OSOBA");
		}
		
		if (t.getMaxDanaZaOtkazivanje()<0) {
			return new Valid(false, "MAX_DANA");
		}
		
		if (t.getCenaProlece()<=0) {
			return new Valid(false, "CENA_PROLECE");
		}
		
		if (t.getCenaLeto()<=0) {
			return new Valid(false, "CENA_LETO");
		}
		
		if (t.getCenaJesen()<=0) {
			return new Valid(false, "CENA_JESEN");
		}
		
		if (t.getCenaZima()<=0) {
			return new Valid(false, "CENA_ZIMA");
		}
		
		if (t.getVlasnik()==null) {
			return new Valid(false, "VLASNIK_NULL");
		}
		
		return new Valid(true, "");
	}
}
