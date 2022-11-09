package org.alkemy.wallet.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
		
		if (user==null) {
			throw new NotFoundException("User not found: " + email);
		}
		
		if(user.getSoftDelete().equals(true)) {
			throw new NotFoundException("User not found: " + email);	
		}
		
		Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRole());
		
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);		
	}
	
	private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
			    List<GrantedAuthority> authorities = new ArrayList<>();
			    String prefix = "ROLE_";
			    authorities.add(new SimpleGrantedAuthority(prefix + role.getRoleName().name()));
			    return authorities;
	}
}