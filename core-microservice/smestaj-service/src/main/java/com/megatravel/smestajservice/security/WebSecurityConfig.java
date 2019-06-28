package com.megatravel.smestajservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtTokenUtils jwtTokenProvider;

	@Autowired
	private UserDetailsService userDetailsService;

	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable()                                       
        .authorizeRequests()
        .antMatchers("/login/*", "/auth/**", "/auth/*", "/auth/login/**",
        		"/auth/login/*", "/smestaj-service/smestaj-korisnik/all",
        		"/smestaj-service/smestaj-korisnik/bll", "/smestaj-service/smestaj-korisnik/allTipovi", 
        		"/smestaj-service/smestaj-korisnik/allKategorije",
        		"/smestaj-service/smestaj-korisnik/allUsluge",
        		"/smestaj-service/smestaj-korisnik/prosekLat/*",
        		"/smestaj-service/smestaj-korisnik/prosekLong",
        		"/smestaj-service/smestaj-korisnik/rastojanje/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Apply JWT
		http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		//sve sto ce se ignorisati za web
		web.ignoring().antMatchers("/webjars/**")//
				.antMatchers("/public")//
				.antMatchers("/main**")//
				.antMatchers("/inline**")//
				.antMatchers("/polyfills**")//
				.antMatchers("/styles**")//
				.antMatchers("/favicon.ico")//
				.antMatchers("/scripts**")//
				.antMatchers("/glyphicons**")//
				.antMatchers("/fontawesome**")//
				.antMatchers("/vendor**")//
				.antMatchers("/assets/**")//
				.antMatchers("/Poppins**")//
				.antMatchers("/h2-console");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

}