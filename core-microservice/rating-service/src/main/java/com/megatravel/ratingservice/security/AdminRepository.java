package com.megatravel.ratingservice.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories(basePackageClasses = {Admin.class})
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>
{
	@Query(value= "SELECT a FROM Admin a WHERE a.email = ?1")
	public Admin findByEmail(String email);
}
