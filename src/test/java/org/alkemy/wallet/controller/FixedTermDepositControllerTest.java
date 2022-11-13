package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.WalletApplication;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.model.*;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WalletApplication.class)
@AutoConfigureMockMvc
public class FixedTermDepositControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private ObjectMapper jsonMapper;

    private User user, admin;
    private Account userAccount, adminAccount;
    private String userToken, adminToken;
    private FixedTermDepositRequestDto fixedTermDepositRequestDto;
    private Calendar cal = Calendar.getInstance();

    @MockBean private IUserRepository userRepositoryMock;
    @MockBean private IAccountRepository accountRepository;
    @MockBean private IFixedTermDepositRepository fixedTermDepositRepository;

    @BeforeEach
    public void setUp() throws ParseException {
        Role userRole = new Role(2L, RoleName.ADMIN, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "USER Role", new Date(), new Date());

        user = new User(1L, "maxi", "maxi", "maxi@alkemy.com", "12345678", userRole, new Date(), new Date(), false);
        admin = new User(2L, "admin", "admin", "admin@alkemy.com", "12345678", adminRole, new Date(), new Date(), false);

        UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        UserDetails loggedAdminDetails = new org.springframework.security.core.userdetails.User(admin.getEmail(), admin.getPassword(), new ArrayList<>());
        userToken = jwtTokenUtil.generateToken(loggedUserDetails);
        adminToken = jwtTokenUtil.generateToken(loggedAdminDetails);

        cal.add(Calendar.DATE, 35);
        fixedTermDepositRequestDto = new FixedTermDepositRequestDto();
        fixedTermDepositRequestDto.setAmount(120.0);
        fixedTermDepositRequestDto.setCurrency(Currency.ARS);
        fixedTermDepositRequestDto.setClosingDate(cal.getTime());

        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(userRepositoryMock.findByEmail(admin.getEmail())).thenReturn(admin);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.ofNullable(admin));

        userAccount = new Account(1L, Currency.ARS, 300000D, 50000D, user, new Date(), new Date(), false);
        adminAccount = new Account(2L, Currency.ARS, 350000D, 70000D, admin, new Date(), new Date(), false);
        when(accountRepository.findByCurrencyAndUser(fixedTermDepositRequestDto.getCurrency(), user)).thenReturn(userAccount);
        when(accountRepository.findByCurrencyAndUser(fixedTermDepositRequestDto.getCurrency(), admin)).thenReturn(adminAccount);
        when(fixedTermDepositRepository.save(any(FixedTermDeposit.class))).thenAnswer(i -> i.getArguments()[0]);
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
        mockMvc.perform(post("/fixedDeposit/simulate").content(requestJson)
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
        mockMvc.perform(post("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", Matchers.is(fixedTermDepositSimulateDto.getTotalAmount())));
    }
    @Test
    void simulateFixedTermDeposit_WrongTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(post("/fixedDeposit/simulate").
                        header("Authorization", "Bearer " + userToken + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }
    @Test
    void simulateFixedTermDeposit_NoTokenProvided_UnauthorizedResponse() throws Exception{
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);
        mockMvc.perform(post("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void simulateFixedTermDeposit_TimeLessThan30Days_OkResponse() throws Exception{
        cal.add(Calendar.DATE, -30);
        fixedTermDepositRequestDto.setClosingDate(cal.getTime());
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);

        mockMvc.perform(post("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }
    @Test
    void simulateFixedTermDeposit_AmountEqual0_BadRequestResponse() throws Exception {
        fixedTermDepositRequestDto.setAmount(0.0);
        String requestJson = jsonMapper.writeValueAsString(fixedTermDepositRequestDto);
        mockMvc.perform(post("/fixedDeposit/simulate").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }
    @Test
    void create_userToken_CreatedResponse() throws Exception{
        Long depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        Double interest = fixedTermDepositRequestDto.getAmount() * 0.05 * depositDuration;
        Double originalBalance = userAccount.getBalance();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/fixedDeposit")
                .content(jsonMapper.writeValueAsString(fixedTermDepositRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", Matchers.is(fixedTermDepositRequestDto.getAmount())))
                .andExpect(jsonPath("$.interest", Matchers.is(interest)));

        assertEquals(originalBalance - fixedTermDepositRequestDto.getAmount(), userAccount.getBalance());
    }
    @Test
    void create_adminToken_CreatedResponse() throws Exception{
        Long depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        Double interest = fixedTermDepositRequestDto.getAmount() * 0.05 * depositDuration;
        Double originalBalance = adminAccount.getBalance();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/fixedDeposit")
                .content(jsonMapper.writeValueAsString(fixedTermDepositRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", Matchers.is(fixedTermDepositRequestDto.getAmount())))
                .andExpect(jsonPath("$.interest", Matchers.is(interest)));

        assertEquals(originalBalance - fixedTermDepositRequestDto.getAmount(), adminAccount.getBalance());
    }
    @Test
    void create_NoTokenProvided_UnauthorizedResponse() throws Exception{
        Double adminOriginalBalance = adminAccount.getBalance();
        Double userOriginalBalance = userAccount.getBalance();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/fixedDeposit")
                .content(jsonMapper.writeValueAsString(fixedTermDepositRequestDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        assertEquals(userOriginalBalance, userAccount.getBalance());
        assertEquals(adminOriginalBalance, adminAccount.getBalance());
    }

}
