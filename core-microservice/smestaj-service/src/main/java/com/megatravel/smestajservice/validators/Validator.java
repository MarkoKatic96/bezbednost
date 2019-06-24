package com.megatravel.smestajservice.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
