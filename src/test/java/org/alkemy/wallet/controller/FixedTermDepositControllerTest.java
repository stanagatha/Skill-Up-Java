package org.alkemy.wallet.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.WalletApplication;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WalletApplication.class)
@AutoConfigureMockMvc

public class FixedTermDepositControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private ObjectMapper jsonMapper;
    @Autowired private UserMapper userMapper;
    private User user, admin;
    private String userToken, adminToken;
    private FixedTermDepositRequestDto fixedTermDepositRequestDto;
    private Calendar cal = Calendar.getInstance();

    @MockBean private IUserRepository userRepositoryMock;


    @BeforeEach
    public void setUp() throws ParseException {
        Role userRole = new Role(2L, RoleName.ADMIN, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "USER Role", new Date(), new Date());

        user = new User("maxi", "maxi", "maxi@alkemy.com", "12345678", userRole);
        admin = new User("admin", "admin", "admin@alkemy.com", "12345678", adminRole);
        user.setId(1L);
        UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        UserDetails loggedAdminDetails = new org.springframework.security.core.userdetails.User(admin.getEmail(), admin.getPassword(), new ArrayList<>());
        userToken = jwtTokenUtil.generateToken(loggedUserDetails);
        cal.add(Calendar.DATE, 35);
        fixedTermDepositRequestDto = new FixedTermDepositRequestDto();
        fixedTermDepositRequestDto.setAmount(120.0);
        fixedTermDepositRequestDto.setCurrency(Currency.ARS);
        fixedTermDepositRequestDto.setClosingDate(cal.getTime());
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(userRepositoryMock.findByEmail(admin.getEmail())).thenReturn(admin);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.ofNullable(admin));

    }

    @Test
    void simulateFixedTermDeposit_UserTokenProvided_OkResponse() throws Exception{
        Long depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);
        FixedTermDepositSimulateDto fixedTermDepositSimulateDto = new FixedTermDepositSimulateDto();
        fixedTermDepositSimulateDto.setAmount(fixedTermDepositRequestDto.getAmount());
        fixedTermDepositSimulateDto.setCreationDate(new SimpleDateFormat("dd-MM-yyyy").parse("10-11-2022"));
        fixedTermDepositSimulateDto.setClosingDate(fixedTermDepositRequestDto.getClosingDate());
        fixedTermDepositSimulateDto.setInterest(fixedTermDepositRequestDto.getAmount() * 0.05 * depositDuration);
        fixedTermDepositSimulateDto.setTotalAmount(fixedTermDepositRequestDto.getAmount() + fixedTermDepositSimulateDto.getInterest());
        mockMvc.perform(get("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", Matchers.is(fixedTermDepositSimulateDto.getTotalAmount())));
    }
    @Test
    void simulateFixedTermDeposit_AdminTokenProvided_OkResponse() throws Exception{
        Long depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);
        FixedTermDepositSimulateDto fixedTermDepositSimulateDto = new FixedTermDepositSimulateDto();
        fixedTermDepositSimulateDto.setAmount(fixedTermDepositRequestDto.getAmount());
        fixedTermDepositSimulateDto.setCreationDate(new SimpleDateFormat("dd-MM-yyyy").parse("10-11-2022"));
        fixedTermDepositSimulateDto.setClosingDate(fixedTermDepositRequestDto.getClosingDate());
        fixedTermDepositSimulateDto.setInterest(fixedTermDepositRequestDto.getAmount() * 0.05 * depositDuration);
        fixedTermDepositSimulateDto.setTotalAmount(fixedTermDepositRequestDto.getAmount() + fixedTermDepositSimulateDto.getInterest());
        mockMvc.perform(get("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", Matchers.is(fixedTermDepositSimulateDto.getTotalAmount())))
                .andExpect(jsonPath("$.closingDate", Matchers.is(fixedTermDepositSimulateDto.getClosingDate())));
    }
    @Test
    void simulateFixedTermDeposit_WrongTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/fixedDeposit/simulate").
                        header("Authorization", "Bearer " + userToken + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }
    @Test
    void simulateFixedTermDeposit_NoTokenProvided_UnauthorizedResponse() throws Exception{
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);
        mockMvc.perform(get("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void simulateFixedTermDeposit_TimeLessThan30Days_OkResponse() throws Exception{
        cal.add(Calendar.DATE, -30);
        fixedTermDepositRequestDto.setClosingDate(cal.getTime());
        Long depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);

        mockMvc.perform(get("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }
    @Test
    void setFixedTermDeposit_AmountEqual0_BadRequestResponse() throws Exception {
        fixedTermDepositRequestDto.setAmount(0.0);
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);
        mockMvc.perform(get("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }
}
