package com.megatravel.authservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import com.megatravel.authservice.model.users.Admin;
import com.megatravel.authservice.model.users.Agent;
import com.megatravel.authservice.model.users.Korisnik;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String lozinka;
	
	@NotBlank
	@ManyToOne
	private Rola role;

	public User() {}
	
	public User(Admin admin, Rola rola) {
		this.email = admin.getEmail();
		this.lozinka = admin.getLozinka();
		this.role = rola;
	}
	
	public User(Agent agent, Rola rola) {
		this.email = agent.getEmail();
		this.lozinka = agent.getLozinka();
		this.role = rola;
	}
	
	public User(Korisnik korisnik, Rola rola) {
		this.email = korisnik.getEmail();
		this.lozinka = korisnik.getLozinka();
		this.role = rola;
	}
	public String getEmail() {
		return email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public @NotBlank Rola getRola() {
		return role;
	}

	public void setEmail(@NotBlank String email) {
		this.email = email;
	}

	public void setLozinka(@NotBlank String lozinka) {
		this.lozinka = lozinka;
	}

	public void setRola(@NotBlank Rola rola) {
		this.role = rola;
	}
}
