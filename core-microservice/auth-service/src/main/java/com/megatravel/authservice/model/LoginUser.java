package com.megatravel.authservice.model;

public class LoginUser {
	
	private String email;
	
	private String lozinka;
	
	public LoginUser() {}

	public LoginUser(String email, String lozinka) {
		this.email = email;
		this.lozinka = lozinka;
	}

	public String getEmail() {
		return email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}
}
