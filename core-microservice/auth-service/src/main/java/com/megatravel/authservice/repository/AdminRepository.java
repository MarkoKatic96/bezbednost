package com.megatravel.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.megatravel.authservice.model.users.Admin;

@EnableJpaRepositories(basePackageClasses = {Admin.class})
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>
{
	@Query(value= "SELECT a FROM Admin a WHERE (a.email = ?1 AND a.lozinka = ?2)")
	public Admin findByEmailPassword(String email, String lozinka);
	
	@Query(value= "SELECT a FROM Admin a WHERE a.email = ?1")
	public Admin findByEmail(String email);
}
