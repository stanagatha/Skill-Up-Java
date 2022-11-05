package org.alkemy.wallet.controller;

import java.util.Date;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.dto.UserRegisterDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IRoleService;
import org.alkemy.wallet.service.IUserService;
import org.alkemy.wallet.service.impl.UserDetailsServiceImpl;
import org.alkemy.wallet.service.impl.UserServiceImpl;
import org.alkemy.wallet.dto.UserLoginDto;
import org.alkemy.wallet.dto.ResponseJwtDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IRoleService roleService;
	
	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody UserLoginDto authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getEmail());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return new ResponseEntity<ResponseJwtDto>(new ResponseJwtDto(token),null,HttpStatus.OK);
	}

	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping(value = "/register")
	public ResponseEntity<ResponseJwtDto> createUser(@RequestBody UserRegisterDto user) throws Exception {
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		User userSaved = userService.save(new User(
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getPassword(),
				roleService.findByRoleName(RoleName.USER)
				));
		
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(userSaved.getEmail());
		
		userService.createAccounts(userSaved);
				
		final String token = jwtTokenUtil.generateToken(userDetails);
	
		return new ResponseEntity<ResponseJwtDto>(new ResponseJwtDto(token),null,HttpStatus.CREATED);
	}
	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}