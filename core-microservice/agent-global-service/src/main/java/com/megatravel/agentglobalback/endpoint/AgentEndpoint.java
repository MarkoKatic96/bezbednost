package com.megatravel.agentglobalback.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.megatravel.agentglobalback.dto.AgentDTO;
import com.megatravel.agentglobalback.model.Agent;
import com.megatravel.agentglobalback.model.EditRequest;
import com.megatravel.agentglobalback.model.EditResponse;
import com.megatravel.agentglobalback.model.GetAgentByEmailRequest;
import com.megatravel.agentglobalback.model.GetAgentByEmailResponse;
import com.megatravel.agentglobalback.model.GetAgentRequest;
import com.megatravel.agentglobalback.model.GetAgentResponse;
import com.megatravel.agentglobalback.model.NeaktiviranAgent;
import com.megatravel.agentglobalback.model.SignUpRequest;
import com.megatravel.agentglobalback.model.SignUpResponse;
import com.megatravel.agentglobalback.model.ValidateTokenRequest;
import com.megatravel.agentglobalback.model.ValidateTokenResponse;
import com.megatravel.agentglobalback.service.AgentService;
import com.megatravel.agentglobalback.service.NeaktiviranAgentService;
import com.megatravel.agentglobalbackend.validators.AgentRegistracijaDTOValidator;
import com.megatravel.agentglobalbackend.validators.AgentValidator;
import com.megatravel.agentglobalbackend.validators.Valid;

@Endpoint
public class AgentEndpoint {

	private static final String NAMESPACE_URI = "https://megatravel.com";
	
	private static final Logger log = LoggerFactory.getLogger(AgentEndpoint.class);
	
	@Autowired
	AgentService agentService;
	
	@Autowired
	NeaktiviranAgentService neaktiviranAgentService;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAgentRequest")
	@ResponsePayload
	public GetAgentResponse getAgent(@RequestPayload GetAgentRequest request) {
		log.info("getAgent " + request.getId());
		
		GetAgentResponse response = new GetAgentResponse();
		Agent agent = agentService.findOne(request.getId());
		if (agent==null) {
			return response;
		}
		response.setAgent(new AgentDTO(agent));
		return response;
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAgentByEmailRequest")
	@ResponsePayload
	public GetAgentByEmailResponse getAgentByEmail(@RequestPayload GetAgentByEmailRequest request) {
		log.info("getAgentByEmail " + request.getEmail());
		
		GetAgentByEmailResponse response = new GetAgentByEmailResponse();
		Agent agent = agentService.findByEmail(request.getEmail());
		response.setAgent(agent);
		return response;
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "editRequest")
	@ResponsePayload
	public EditResponse edit(@RequestPayload EditRequest request) {
		log.info("edit " + request.getAgent());
		
		EditResponse response = new EditResponse();
		if (request.getAgent()==null) {
			return response;
		}
		
		Valid v = new AgentValidator().validate(request.getAgent());
		if (!v.isValid()) {
			return response;
		}
		
		Agent tempKorisnik = agentService.findByEmail(request.getAgent().getEmail());
		if(tempKorisnik != null) {
			if (tempKorisnik.getIdAgenta()!=request.getAgent().getIdAgenta()) {
				//mora biti jedinstveni mail za korisnika
				return response;
			}
		}
		
		tempKorisnik = agentService.findByPMB(request.getAgent().getPoslovniMaticniBroj());
		if(tempKorisnik != null) {
			if (tempKorisnik.getIdAgenta()!=request.getAgent().getIdAgenta()) {
				//mora biti jedinstveni PMB za korisnika
				return response;
			}
		}
		
		request.getAgent().setLozinka(passwordEncoder.encode(request.getAgent().getLozinka()));
		response.setAgent(new AgentDTO(agentService.save(request.getAgent())));
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "signUpRequest")
	@ResponsePayload
	public SignUpResponse signUp(@RequestPayload SignUpRequest request) {
		log.info("signUp " + request.getAgentRegistracijaDTO());
		
		SignUpResponse response = new SignUpResponse();
		
		if (request.getAgentRegistracijaDTO()==null) {
			return response;
		}
		
		Valid v = new AgentRegistracijaDTOValidator().validate(request.getAgentRegistracijaDTO());
		if (!v.isValid()) {
			return response;
		}
		
		Agent tempKorisnik = agentService.findByEmail(request.getAgentRegistracijaDTO().getEmail());
		if(tempKorisnik != null) {
			//mora biti jedinstveni mail za korisnika
			return response;
		}
		
		tempKorisnik = agentService.findByPMB(request.getAgentRegistracijaDTO().getPoslovniMaticniBroj());
		if(tempKorisnik != null) {
			//mora biti jedinstveni PIM za korisnika
			return response;
		}
		
		NeaktiviranAgent tempKorisnik2 = neaktiviranAgentService.findByEmail(request.getAgentRegistracijaDTO().getEmail());
		if(tempKorisnik2 != null) {
			//mora biti jedinstveni mail za korisnika
			return response;
		}
		
		tempKorisnik2 = neaktiviranAgentService.findByPMB(request.getAgentRegistracijaDTO().getPoslovniMaticniBroj());
		if(tempKorisnik2 != null) {
			//mora biti jedinstveni PIM za korisnika
			return response;
		}
		
		NeaktiviranAgent agent = new NeaktiviranAgent(null, request.getAgentRegistracijaDTO().getIme(), request.getAgentRegistracijaDTO().getPrezime(), request.getAgentRegistracijaDTO().getPoslovniMaticniBroj(), request.getAgentRegistracijaDTO().getEmail());
		NeaktiviranAgent retValue = neaktiviranAgentService.save(agent);
		response.setNeaktiviranAgent(retValue);
		
		return response;
	}
	
	@PreAuthorize("hasAnyRole('ROLE_AGENT')")
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "validateTokenRequest")
	@ResponsePayload
	public ValidateTokenResponse validateToken(@RequestPayload ValidateTokenRequest request) {
		log.info("validateToken " + request.getToken());
		
		ValidateTokenResponse response = new ValidateTokenResponse();
		response.setValid(new Boolean(true));
		return response;
	}
	
	
}
