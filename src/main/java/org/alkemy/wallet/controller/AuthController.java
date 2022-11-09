package org.alkemy.wallet.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alkemy.wallet.dto.ResponseJwtDto;
import org.alkemy.wallet.dto.UserLoginDto;
import org.alkemy.wallet.dto.UserRegisterRequestDto;
import org.alkemy.wallet.dto.UserRegisterResponseDto;
import org.alkemy.wallet.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "AuthController")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate as an already existing user",
               description = "Required information: email, password")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserLoginDto authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        return new ResponseEntity<>(new ResponseJwtDto(
                authService.createToken(authenticationRequest.getEmail())), null, HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
               description = "Required information: first name, last name, email, password.")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<UserRegisterResponseDto> createUser(@RequestBody UserRegisterRequestDto user) throws Exception {
        return new ResponseEntity<>(
                authService.createUserWithAccounts(user),null, HttpStatus.CREATED);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authService.authenticate(username, password);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}