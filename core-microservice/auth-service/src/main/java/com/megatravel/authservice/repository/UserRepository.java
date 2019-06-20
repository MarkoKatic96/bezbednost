package com.megatravel.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.megatravel.authservice.model.User;

@EnableJpaRepositories(basePackageClasses= {User.class})
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String userId);

}
