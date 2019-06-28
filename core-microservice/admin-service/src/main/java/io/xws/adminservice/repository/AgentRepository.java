package io.xws.adminservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import io.xws.adminservice.model.Agent;

@EnableJpaRepositories(basePackageClasses= {Agent.class})
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

	public Optional<Agent> findById(Long id);
}