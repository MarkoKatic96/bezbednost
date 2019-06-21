package com.megatravel.agentlocalbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.megatravel.agentlocalbackend.model.Agent;
import com.megatravel.agentlocalbackend.repository.AgentRepository;

@Component
public class AgentService {
	
	@Autowired
	private AgentRepository agentRepository;
	
	//@Autowired
	//private JwtTokenUtils jwtTokenProvider;

	//@Autowired
	//private AuthenticationManager authenticationManager;
	
	//@Autowired
	//private PasswordEncoder passwordEncoder;
	
	public Agent findByEmail(String email) {
		return agentRepository.findByEmail(email);
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
	
	public void deleteAll() {
		agentRepository.deleteAll();
	}
	
}
