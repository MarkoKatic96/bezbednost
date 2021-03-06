package io.xws.adminservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.xws.adminservice.model.KategorijaSmestaja;

@Repository
public interface KategorijaSmestajaRepository extends JpaRepository<KategorijaSmestaja, Long>{

	@Query(value = "SELECT * FROM kategorija_smestaja", nativeQuery=true)
	List<KategorijaSmestaja> getAll();
	
}
