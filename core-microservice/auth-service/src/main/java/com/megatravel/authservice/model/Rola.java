package com.megatravel.authservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class Rola {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String nazivRole;
	
	public Rola() {}

	public Long getId() {
		return id;
	}

	public String getNazivRole() {
		return nazivRole;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNazivRole(String nazivRole) {
		this.nazivRole = nazivRole;
	}
}
