package com.megatravel.authservice.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.megatravel.authservice.model.LoginUser;
import com.megatravel.authservice.model.MyUserDetails;
import com.megatravel.authservice.model.User;
import com.megatravel.authservice.repository.UserRepository;
import com.megatravel.authservice.service.UserService;

import io.jsonwebtoken.Jwts;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {
   
	private static final String AUTH="auth";
	
	@Value("${jwt.security.key}")
    private String jwtKey;
	
	@Autowired
	private UserRepository userDao;   
   
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;      
   
	@Override
	public User save(LoginUser user) {
		User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setLozinka(passwordEncoder.encode(user.getLozinka()));
        return userDao.save(newUser);
	}
   
	public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
		
		User user = userDao.findByEmail(token);
        if(user == null){
        	throw new UsernameNotFoundException("Invalid username or password.");
        }
        
        List<String> roleList = getRoleList(token);
        UserDetails userDetails = new MyUserDetails(user.getEmail(), roleList.toArray(new String[roleList.size()]));
        return userDetails;
	}

	@SuppressWarnings("unchecked")
	private List<String> getRoleList(String token) {
		 return (List<String>) Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody().get(AUTH);
	}

		// Other service methods
	
}