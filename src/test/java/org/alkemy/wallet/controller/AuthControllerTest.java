package org.alkemy.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;

import org.alkemy.wallet.dto.ResponseJwtDto;
import org.alkemy.wallet.dto.UserLoginDto;
import org.alkemy.wallet.dto.UserRegisterRequestDto;
import org.alkemy.wallet.dto.UserRegisterResponseDto;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IRoleRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.alkemy.wallet.service.IAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {
	
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper jsonMapper;    
	@Autowired private UserMapper userMapper;
    //@MockBean private IAuthService authServiceMock;
	@MockBean private IAccountService accountService;
	
	@MockBean private UserDetailsService userDetailsService;
	@MockBean private IUserRepository userRepository;
	@MockBean private IRoleRepository roleRepository;
	@MockBean private AuthenticationManager authenticationManager;	
	
	// Simulate object when needed
	@SpyBean private JwtTokenUtil jwtTokenUtil;
	
	private UserRegisterRequestDto userRegisterRequestDto;
	private UserRegisterResponseDto userCreated;
    private UserLoginDto userLoginDto;
    private UserDetails loggedUser1Details;
	private User user1;
	private String token;
    
    
    @BeforeEach
    private void setUp(){
    	token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXNkU0dGZmRzc3RAbG9jYWwuY29tIiwiaWF0IjoxNjY4MDU4MjMwLCJleHAiOjE2NjgwNzYyMzB9.hoN0oCuDDBG54xZEvRdvpG0Th6cvbBPn_QANHWraGcUxTRxvr5NK6M4oQk5T2oWL2HEpikmaJAsSpcan5yIRmg";

        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());

        user1 = new User("User1FN", "User1LN", "user1Email@email.com", "1234", userRole);
        user1.setId(1L);
        
        userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(user1.getEmail());
        userLoginDto.setPassword(user1.getPassword());
        
        loggedUser1Details = new org.springframework.security.core.userdetails.User(user1.getEmail(), user1.getPassword(), new ArrayList<>());

		userRegisterRequestDto = new UserRegisterRequestDto();

		userRegisterRequestDto.setEmail("user1Email@email.com");
		userRegisterRequestDto.setFirstName("User1FN");
		userRegisterRequestDto.setLastName("User2FN");
		userRegisterRequestDto.setPassword("123456");
		        
		userCreated = userMapper.userToUserRegisterResponseDTO(user1);
		userCreated.setToken(token);

		when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(userRole);
		when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user1);
		when(userDetailsService.loadUserByUsername(user1.getEmail())).thenReturn(loggedUser1Details);
					
    }
	
	@Test
	public void createUser_RegisterANewUser_CreatedResponse() throws JsonProcessingException, Exception {		
		String expectedJson = jsonMapper.writeValueAsString(userCreated);		

		// Use a static token		
		when(jwtTokenUtil.generateToken(loggedUser1Details)).thenReturn(token);
		
		mockMvc.perform(post("/auth/register").content(
				jsonMapper.writeValueAsString(userRegisterRequestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedJson));;
	}
	
	@Test
	public void createUser_InvalidBody_CreatedResponse() throws JsonProcessingException, Exception {		
						
		mockMvc.perform(post("/auth/register").content(
				jsonMapper.writeValueAsString("{}"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void createAuthenticationToken_DynamicToken_OkResponse() throws JsonProcessingException, Exception {		
		
		// A true token is generated		
		mockMvc.perform(post("/auth/login").content(
				jsonMapper.writeValueAsString(userLoginDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
	
	@Test
	public void createAuthenticationToken_LoginUser_OkResponse() throws JsonProcessingException, Exception {		
		String expectedJson = jsonMapper.writeValueAsString(new ResponseJwtDto(token));		
			
		// Use a static token
		when(jwtTokenUtil.generateToken(loggedUser1Details)).thenReturn(token);
		
		mockMvc.perform(post("/auth/login").content(
				jsonMapper.writeValueAsString(userLoginDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));;
	}
		
	@Test
	public void createAuthenticationToken_InvalidBody_BadRequestResponse() throws JsonProcessingException, Exception {		

		mockMvc.perform(post("/auth/login").content(
				jsonMapper.writeValueAsString("invalid json"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void createAuthenticationToken_InvalidEndpoint_BadRequestResponse() throws JsonProcessingException, Exception {		
		
		mockMvc.perform(post("/auth/logininvalid").content(
				jsonMapper.writeValueAsString(userLoginDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	} 
}