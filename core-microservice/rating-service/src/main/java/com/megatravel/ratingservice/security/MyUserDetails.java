package com.megatravel.ratingservice.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

	@Autowired
    private AdminRepository adminRepository;
	
	@Autowired
    private AgentRepository agentRepository;
	
	@Autowired
    private KorisnikRepository korisnikRepository;

	//izvuce iz baze usere koji na m trebaju i automatksi se aktivira
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		String rola = "";
		String lozinka = "";
		
		if (korisnikRepository.findByEmail(email)!=null) {
        	rola = "ROLE_KORISNIK";
        	lozinka = korisnikRepository.findByEmail(email).getLozinka();
        } else if (agentRepository.findByEmail(email)!=null) {
        	rola = "ROLE_AGENT";
        	lozinka = agentRepository.findByEmail(email).getLozinka();
        } else if (adminRepository.findByEmail(email)!=null) {
        	rola = "ROLE_ADMIN";
        	lozinka = adminRepository.findByEmail(email).getLozinka();
        } else {
        	throw new UsernameNotFoundException("Fuck your rola! It doesn't exists moron. BYW email is " + email);
        }
		
		System.out.println("email: " + email + " comes with role: " + rola);
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		grantedAuthorities.add(new SimpleGrantedAuthority(rola));
		//grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		//grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_KORISNIK"));
		//grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_AGENT"));

		return new org.springframework.security.core.userdetails.User(email, lozinka, true, true, true,
				true, grantedAuthorities);
	}

}