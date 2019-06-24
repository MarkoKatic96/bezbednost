package com.megatravel.authservice.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
