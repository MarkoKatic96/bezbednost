package com.megatravel.agentlocalbackend.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
