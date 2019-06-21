package com.megatravel.agentlocalbackend.dto;

import java.util.Date;

import com.megatravel.agentlocalbackend.model.StatusPoruke;

public class PorukaDTO {
	
	private Long idPoruke;	
	private Date datumSlanja;
	private Long posiljalac;
	private String tipPosiljaoca;
	private Long primalac;
	private String tipPrimaoca;
	private String sadrzaj;
	private StatusPoruke status;
	
	public PorukaDTO() {
	}
	
	public Long getIdPoruke() {
		return idPoruke;
	}

	public Date getDatumSlanja() {
		return datumSlanja;
	}

	public void setDatumSlanja(Date datumSlanja) {
		this.datumSlanja = datumSlanja;
	}

	public Long getPosiljalac() {
		return posiljalac;
	}

	public void setPosiljalac(Long posiljalac) {
		this.posiljalac = posiljalac;
	}

	public String getTipPosiljaoca() {
		return tipPosiljaoca;
	}

	public void setTipPosiljaoca(String tipPosiljaoca) {
		this.tipPosiljaoca = tipPosiljaoca;
	}

	public Long getPrimalac() {
		return primalac;
	}

	public void setPrimalac(Long primalac) {
		this.primalac = primalac;
	}

	public String getTipPrimaoca() {
		return tipPrimaoca;
	}

	public void setTipPrimaoca(String tipPrimaoca) {
		this.tipPrimaoca = tipPrimaoca;
	}

	public String getSadrzaj() {
		return sadrzaj;
	}

	public void setSadrzaj(String sadrzaj) {
		this.sadrzaj = sadrzaj;
	}

	public StatusPoruke getStatus() {
		return status;
	}

	public void setStatus(StatusPoruke status) {
		this.status = status;
	}
}
