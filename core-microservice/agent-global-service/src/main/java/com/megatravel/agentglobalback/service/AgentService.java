package com.megatravel.agentglobalback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.megatravel.agentglobalback.model.Agent;
import com.megatravel.agentglobalback.repository.AgentRepository;

@Component
public class AgentService {
	
	@Autowired
	private AgentRepository agentRepository;
	
	public Agent findByEmail(String email) {
		return agentRepository.findByEmail(email);
	}
	
	public Agent findByPMB(Long poslovniMaticniBroj) {
		return agentRepository.findByPoslovniMaticniBroj(poslovniMaticniBroj);
	}

	public Agent findOne(Long id) {
		try{
			return agentRepository.findById(id).get();
		} catch (Exception e) {
			return null;
		}
	}

	public Agent save(Agent agent) {
		return agentRepository.save(agent);
	}
	
}
