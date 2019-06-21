package com.megatravel.agentglobalback.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "agentPrijavaDTO", propOrder = {
    "email",
    "lozinka"
})
public class AgentPrijavaDTO {
	@XmlElement(namespace="https://megatravel.com/email")
    private String email;
	@XmlElement(namespace="https://megatravel.com/lozinka")
    private String lozinka;

	public AgentPrijavaDTO() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}
}
