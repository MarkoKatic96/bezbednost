package com.megatravel.agentglobalback.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.megatravel.agentglobalback.model.Agent;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "agentDTO", propOrder = {
    "datumClanstva",
    "email",
    "idAgenta",
    "ime",
    "poslovniMaticniBroj",
    "prezime"
})
public class AgentDTO {
	
	@XmlSchemaType(name = "dateTime")
    @XmlElement(namespace="https://megatravel.com/datumClanstva")
    private Date datumClanstva;
    
    @XmlElement(namespace="https://megatravel.com/email")
    private String email;
    
    @XmlElement(namespace="https://megatravel.com/idAgenta")
    private Long idAgenta;
    
    @XmlElement(namespace="https://megatravel.com/ime")
    private String ime;
    
    @XmlElement(namespace="https://megatravel.com/poslovniMaticniBroj")
    private Long poslovniMaticniBroj;
    
    @XmlElement(namespace="https://megatravel.com/prezime")
    private String prezime;
    
    public AgentDTO() {
    	
	}

    public AgentDTO(Agent agent) {
		super();
		this.idAgenta = agent.getIdAgenta();
		this.ime = agent.getIme();
		this.prezime = agent.getPrezime();
		this.poslovniMaticniBroj = agent.getPoslovniMaticniBroj();
		this.datumClanstva = agent.getDatumClanstva();
		this.email = agent.getEmail();
	}

	public Long getIdAgenta() {
		return idAgenta;
	}

	public void setIdAgenta(Long idAgenta) {
		this.idAgenta = idAgenta;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public Long getPoslovniMaticniBroj() {
		return poslovniMaticniBroj;
	}

	public void setPoslovniMaticniBroj(Long poslovniMaticniBroj) {
		this.poslovniMaticniBroj = poslovniMaticniBroj;
	}

	public Date getDatumClanstva() {
		return datumClanstva;
	}

	public void setDatumClanstva(Date datumClanstva) {
		this.datumClanstva = datumClanstva;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    
}