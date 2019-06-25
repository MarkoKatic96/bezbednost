package io.webxml.korisnikservice.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
