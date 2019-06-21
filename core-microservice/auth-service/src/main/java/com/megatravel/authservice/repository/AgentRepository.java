package com.megatravel.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.megatravel.authservice.model.users.Agent;

@EnableJpaRepositories(basePackageClasses = {Agent.class})
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>
{
	@Query(value= "SELECT a FROM Agent a WHERE (a.email = ?1 AND a.lozinka = ?2)")
	public Agent findByEmailPassword(String email, String lozinka);
	
	@Query(value= "SELECT a FROM Agent a WHERE a.email = ?1")
	public Agent findByEmail(String email);
}
