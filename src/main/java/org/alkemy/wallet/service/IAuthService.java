package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.UserRegisterRequestDto;
import org.alkemy.wallet.dto.UserRegisterResponseDto;

public interface IAuthService {

	UserRegisterResponseDto createUserWithAccounts(UserRegisterRequestDto user);

	String createToken(String email);
	
	void authenticate(String username, String password);
}
