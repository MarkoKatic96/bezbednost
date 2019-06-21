package com.megatravel.agentlocalbackend.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MyUserDetails implements UserDetailsService {

	@Autowired
    private RestTemplate authService;

	//izvuce iz baze usere koji nam trebaju i automatksi se aktivira
	@Override
	public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
		ResponseEntity<UserDAO> user = null;
		
		try {
			user = authService.postForEntity("http://auth-service/auth/login/role", token, UserDAO.class);
		} catch (Exception e) {
			return null;
		}
		if (user.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getBody().getRola()));

		return new org.springframework.security.core.userdetails.User(user.getBody().getEmail(), user.getBody().getLozinka(), true, true, true,
				true, grantedAuthorities);
	}

}