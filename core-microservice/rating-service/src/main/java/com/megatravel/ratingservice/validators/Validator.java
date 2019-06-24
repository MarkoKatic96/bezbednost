package com.megatravel.ratingservice.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
