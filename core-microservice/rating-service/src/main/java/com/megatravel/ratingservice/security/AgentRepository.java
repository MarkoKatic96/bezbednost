package com.megatravel.ratingservice.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories(basePackageClasses = {Agent.class})
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>
{
	@Query(value= "SELECT a FROM Agent a WHERE a.email = ?1")
	public Agent findByEmail(String email);
}
