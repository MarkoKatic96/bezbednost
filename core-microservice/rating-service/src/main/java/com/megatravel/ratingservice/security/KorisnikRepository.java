package com.megatravel.ratingservice.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories(basePackageClasses = {Korisnik.class})
@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, Long>
{
	@Query(value= "SELECT k FROM Korisnik k WHERE k.email = ?1")
	public Korisnik findByEmail(String email);
}
