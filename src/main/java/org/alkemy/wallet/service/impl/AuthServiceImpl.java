package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.UserRegisterRequestDto;
import org.alkemy.wallet.dto.UserRegisterResponseDto;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IRoleRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IAuthService;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IAccountService accountService;

	@Autowired
	private IRoleRepository roleRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	UserMapper userMapper;
	
	@Override
	public UserRegisterResponseDto createUserWithAccounts(UserRegisterRequestDto user) {
		
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		
		User userSaved = userService.save(new User(
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getPassword(),
				roleRepository.findByRoleName(RoleName.USER)
				));
		
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(userSaved.getEmail());
		
		accountService.createAccount(userSaved, Currency.ARS);
		accountService.createAccount(userSaved, Currency.USD);

		final String token = jwtTokenUtil.generateToken(userDetails);
		
		UserRegisterResponseDto userCreated = userMapper.userToUserRegisterResponseDTO(userSaved);

		userCreated.setToken(token);
					
		return userCreated;
	}

	@Override
	public String createToken(String email) {
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		return jwtTokenUtil.generateToken(userDetails);
		
	}

	@Override
	public void authenticate(String username, String password) {
		
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		
	}

}
