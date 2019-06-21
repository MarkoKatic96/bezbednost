package com.megatravel.agentlocalbackend.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.megatravel.agentlocalbackend.model.Rezervacija;

@EnableJpaRepositories(basePackageClasses= {Rezervacija.class})
@Repository
public interface RezervacijaRepository extends JpaRepository<Rezervacija, Long> {

	@Query(value= "SELECT MIN(r.updateTimestamp) FROM Rezervacija r")
	Date findOldestDate();
}
