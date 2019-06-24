package io.webxml.reservationservice.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
