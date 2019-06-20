package com.megatravel.authservice.service;

import org.springframework.stereotype.Service;

import com.megatravel.authservice.model.LoginUser;
import com.megatravel.authservice.model.User;

@Service
public interface UserService {

	User save(LoginUser user);
}
