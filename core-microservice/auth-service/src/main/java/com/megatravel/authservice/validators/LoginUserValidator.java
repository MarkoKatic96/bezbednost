package com.megatravel.authservice.validators;

import java.util.regex.Pattern;

import com.megatravel.authservice.model.LoginUser;

public class LoginUserValidator implements Validator<LoginUser> {

	@Override
	public Valid validate(LoginUser t) {
		if (t==null) {
			return new Valid(false, "NULL");
		}
		
		if (t.getEmail()==null) {
			return new Valid(false, "EMAIL_NULL");
		}
		
		if (t.getLozinka()==null) {
			return new Valid(false, "LOZINKA_NULL");
		}
		
		if (t.getEmail().trim().isEmpty()) {
			return new Valid(false, "EMAIL_EMPTY");
		}
		
		if (t.getLozinka().trim().isEmpty()) {
			return new Valid(false, "LOZINKA_EMPTY");
		}
		
		if (!Pattern.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@megatravel\\.com$", t.getEmail().trim())) {
			return new Valid(false, "EMAIL_CHAR");
		}
		
		/**
		 * 
		^                 # start-of-string
		(?=.*[0-9])       # a digit must occur at least once
		(?=.*[a-z])       # a lower case letter must occur at least once
		(?=.*[A-Z])       # an upper case letter must occur at least once
		(?=.*[@#$%^&+=])  # a special character must occur at least once
		(?=\S+$)          # no whitespace allowed in the entire string
		.{8,}             # anything, at least eight places though
		$                 # end-of-string
		*/
		if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", t.getLozinka().trim())) {
			return new Valid(false, "LOZINKA_CHAR");
		}
		
		return new Valid(true, "");
	}

}
