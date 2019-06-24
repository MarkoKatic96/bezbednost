package com.megatravel.agentglobalbackend.validators;

public interface Validator<T> {
	
	public Valid validate(T t);
}
