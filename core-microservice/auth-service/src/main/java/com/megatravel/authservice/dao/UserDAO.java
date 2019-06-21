package com.megatravel.authservice.dao;

public class UserDAO {
	private String email;
	private String lozinka;
	private String rola;
	
	public UserDAO(String email, String lozinka, String rola) {
		super();
		this.email = email;
		this.lozinka = lozinka;
		this.rola = rola;
	}
	
	public UserDAO() {
	
	}

	public String getEmail() {
		return email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public String getRola() {
		return rola;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public void setRola(String rola) {
		this.rola = rola;
	}
}
