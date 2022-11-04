package org.alkemy.wallet.service.impl;

import java.util.ArrayList;

import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired 
	IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) {
		User user =  userRepository.findByEmail(email);
		
		if (user!=null) {			
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					new ArrayList<>());
		} else {
			throw new NotFoundException("User not found: " + email); 
		}
	}
}