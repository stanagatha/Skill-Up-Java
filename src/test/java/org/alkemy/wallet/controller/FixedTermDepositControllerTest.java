package org.alkemy.wallet.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FixedTermDepositControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private ObjectMapper jsonMapper;
    @Autowired private UserMapper userMapper;
    private User user;
    private String userToken;
    private FixedTermDepositRequestDto fixedTermDepositRequestDto;
    @MockBean private IUserRepository userRepositoryMock;


    @BeforeEach
    public void setUp() throws ParseException {
        Role userRole = new Role(2L, RoleName.ADMIN, "USER Role", new Date(), new Date());

        user = new User("Natalia", "Gamarra", "nataliag@alkemy.com", "12345678", userRole);
        user.setId(1L);
            UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        userToken = jwtTokenUtil.generateToken(loggedUserDetails);

        fixedTermDepositRequestDto = new FixedTermDepositRequestDto();
        fixedTermDepositRequestDto.setAmount(120.0);
        fixedTermDepositRequestDto.setCurrency(Currency.ARS);
        fixedTermDepositRequestDto.setClosingDate(new SimpleDateFormat("dd-MM-yyyy").parse("14-12-2022"));
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.ofNullable(user));


    }

    @Test
    void simulateFixedTermDeposit_OkResponse() throws Exception{
        Long depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);

        FixedTermDepositSimulateDto fixedTermDepositSimulateDto = new FixedTermDepositSimulateDto();
        fixedTermDepositSimulateDto.setAmount(fixedTermDepositRequestDto.getAmount());
        fixedTermDepositSimulateDto.setCreationDate(new SimpleDateFormat("dd-MM-yyyy").parse("10-11-2022"));
        fixedTermDepositSimulateDto.setClosingDate(fixedTermDepositRequestDto.getClosingDate());
        fixedTermDepositSimulateDto.setInterest(fixedTermDepositRequestDto.getAmount() * 0.05 * depositDuration);
        fixedTermDepositSimulateDto.setTotalAmount(fixedTermDepositRequestDto.getAmount() + fixedTermDepositSimulateDto.getInterest());
        String expectedJson = jsonMapper.writeValueAsString(fixedTermDepositSimulateDto);
        mockMvc.perform(get("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
    @Test
    void getCurrent_UserRole_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userMapper.userToUserDTO(user));
        mockMvc.perform(get("/users/current").
                        header("Authorization", "Bearer " + userToken).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }
}
