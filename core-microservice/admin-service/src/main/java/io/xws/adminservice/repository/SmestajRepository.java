package io.xws.adminservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import io.xws.adminservice.model.Smestaj;

@EnableJpaRepositories(basePackageClasses= {Smestaj.class})
@Repository
public interface SmestajRepository extends JpaRepository<Smestaj, Long> {
	
	@Query(value= "SELECT DISTINCT s FROM Smestaj s WHERE s.vlasnik = ?1 ORDER BY s.idSmestaja")
	Page<Smestaj> findAllFromMe(Long idVlasnika, Pageable page);
	
	@Query(value = "SELECT * FROM Smestaj", nativeQuery = true)
	List<Smestaj> pronadjiSve();

	@Query(value= "SELECT DISTINCT s FROM Smestaj s WHERE s.adresa.grad = ?1 ORDER BY s.idSmestaja")
	Page<Smestaj> getAllInGrad(String grad, Pageable page);

	@Query(value= "SELECT DISTINCT s FROM Smestaj s WHERE s.tipSmestaja = ?1 ORDER BY s.idSmestaja")
	Page<Smestaj> getAllOfTip(Long tip, Pageable page);
	
	@Query(value= "SELECT DISTINCT s FROM Smestaj s WHERE s.kategorijaSmestaja = ?1 ORDER BY s.idSmestaja")
	Page<Smestaj> getAllOfKategorija(Long kategorija, Pageable page);
	
}
