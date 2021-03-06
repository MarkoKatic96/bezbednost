package io.xws.adminservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.xws.adminservice.model.Korisnik;

//@EnableJpaRepositories(basePackageClasses= {Korisnik.class})
@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, Long>{

}
