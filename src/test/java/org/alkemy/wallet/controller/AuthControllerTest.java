package org.alkemy.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alkemy.wallet.dto.UserRegisterRequestDto;
import org.alkemy.wallet.dto.UserRegisterResponseDto;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IRoleRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IAuthService;
import org.alkemy.wallet.service.IUserService;
import org.alkemy.wallet.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.mockito.junit.jupiter.MockitoExtension;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {
	
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper jsonMapper;    
	@Autowired private UserMapper userMapper;
    @MockBean private IAuthService authServiceMock;
	    
	private UserRegisterRequestDto userRegisterRequestDto;
	private UserRegisterResponseDto userCreated;
    private User user1;
    
    @BeforeEach
    private void setUp(){
    	String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXNkU0dGZmRzc3RAbG9jYWwuY29tIiwiaWF0IjoxNjY4MDU4MjMwLCJleHAiOjE2NjgwNzYyMzB9.hoN0oCuDDBG54xZEvRdvpG0Th6cvbBPn_QANHWraGcUxTRxvr5NK6M4oQk5T2oWL2HEpikmaJAsSpcan5yIRmg";
    	//String encryptedPasword = "$2a$10$eSxfVSIjlFMa2iF1FoIfoer2iLgzk67TsHGhNR05CRCY8dNJMUk8G";
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());

        user1 = new User("User1FN", "User1LN", "user1Email@email.com", "1234", userRole);
        user1.setId(1L);

		userRegisterRequestDto = new UserRegisterRequestDto();

		userRegisterRequestDto.setEmail("user1Email@email.com");
		userRegisterRequestDto.setFirstName("User1FN");
		userRegisterRequestDto.setLastName("User2FN");
		userRegisterRequestDto.setPassword("123456");
		        
		userCreated = userMapper.userToUserRegisterResponseDTO(user1);
		userCreated.setToken(token);

		
		
    }

	@Test
	public void createUser_RegisterANewUser_CreatedResponse() throws JsonProcessingException, Exception {		
		String expectedJson = jsonMapper.writeValueAsString(userCreated);		
		
		when(authServiceMock.createUserWithAccounts(userRegisterRequestDto)).thenReturn(userCreated);
		
		mockMvc.perform(post("/auth/register").content(
				jsonMapper.writeValueAsString(userRegisterRequestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedJson));;
	}
	

}