package com.megatravel.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.megatravel.authservice.model.Rola;

@EnableJpaRepositories(basePackageClasses = {Rola.class})
@Repository
public interface RolaRepository extends JpaRepository<Rola, Long>
{
	@Query(value= "SELECT r FROM Rola r WHERE r.nazivRole = ?1")
	public Rola findByNazivRole(String nazivRole);
}
