package com.megatravel.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.megatravel.authservice.model.users.Korisnik;

@EnableJpaRepositories(basePackageClasses = {Korisnik.class})
@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, Long>
{
	@Query(value= "SELECT k FROM Korisnik k WHERE (k.email = ?1 AND k.lozinka = ?2)")
	public Korisnik findByEmailPassword(String email, String lozinka);
	
	@Query(value= "SELECT k FROM Korisnik k WHERE k.email = ?1")
	public Korisnik findByEmail(String email);
}
