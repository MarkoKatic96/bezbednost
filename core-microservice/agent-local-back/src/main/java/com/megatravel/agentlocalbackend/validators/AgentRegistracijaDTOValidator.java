package com.megatravel.agentlocalbackend.validators;

import java.util.regex.Pattern;

import com.megatravel.agentlocalbackend.wsdl.AgentRegistracijaDTO;

public class AgentRegistracijaDTOValidator implements Validator<AgentRegistracijaDTO>{

	@Override
	public Valid validate(AgentRegistracijaDTO t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getEmail()==null) {
			return new Valid(false, "EMAIL_NULL");
		}
		
		if (t.getEmail().trim().isEmpty()) {
			return new Valid(false, "EMAIL_EMPTY");
		}
		
		if (t.getIme().trim().isEmpty()) {
			return new Valid(false, "IME_EMPTY");
		}
		
		if (t.getPrezime().trim().isEmpty()) {
			return new Valid(false, "PREZIME_EMPTY");
		}
		
		if (t.getPoslovniMaticniBroj()==0) {
			return new Valid(false, "PMB_NULL");
		}
		
		if (!Pattern.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@megatravel\\.com$", t.getEmail().trim())) {
			return new Valid(false, "EMAIL_CHAR");
		}
		
		if (!Pattern.matches("[\\p{L}\\p{M}]+", t.getIme().trim())) {
			return new Valid(false, "IME_CHAR");
		}
		
		if (!Pattern.matches("[\\p{L}\\p{M}]+", t.getPrezime().trim())) {
			return new Valid(false, "PREZIME_CHAR");
		}
		
		if (t.getPoslovniMaticniBroj().compareTo(new Long(0))<=0) {
			return new Valid(false, "PMB_MIN");
		}
		
		return new Valid(true, "");
	}

}
