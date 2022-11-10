package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.dto.AccountBalanceDto;
import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.mapper.FixedTermDepositMapper;
import org.alkemy.wallet.model.*;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
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

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Per requirements, a user is allowed to own only one account of each currency type.
 * Nevertheless, the endpoint to get the balance of a user was implemented considering the
 * possibility to allow multiple accounts of the same type as this is a quite likely future
 * feature. This is tested here.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private ObjectMapper jsonMapper;
    @Autowired private FixedTermDepositMapper fixedTermDepositMapper;
    @MockBean private IUserRepository userRepositoryMock;
    @MockBean private IAccountRepository accountRepositoryMock;
    @MockBean private IFixedTermDepositRepository fixedTermDepositRepositoryMock;

    // Test fixture
    private String userToken;
    private List<Account> accounts;
    private List<FixedTermDeposit> fixedTermDeposits;

    @BeforeEach
    void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        User user = new User(1L, "User1FN", "User1LN", "user1Email@email.com", "1234", userRole, new Date(), new Date(), false);

        Account account1 = new Account(1L, Currency.ARS, 1500D, 20000D, user, new Date(), new Date(), false);
        Account account2 = new Account(2L, Currency.ARS, 1500D, 35000D, user, new Date(), new Date(), false);
        Account account3 = new Account(3L, Currency.ARS, 1500D, 750D, user, new Date(), new Date(), false);
        Account account4 = new Account(4L, Currency.USD, 1500D, 300D, user, new Date(), new Date(), false);
        Account account5 = new Account(5L, Currency.USD, 1500D, 800D, user, new Date(), new Date(), false);
        accounts = List.of(account1, account2, account3, account4, account5);

        FixedTermDeposit ftd1 = new FixedTermDeposit(1L, 2000D, user, account1, 10D, new Date(), new Date());
        FixedTermDeposit ftd2 = new FixedTermDeposit(2L, 400D, user, account4, 10D, new Date(), new Date());
        fixedTermDeposits = List.of(ftd1, ftd2);

        UserDetails loggedUser1Details = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        userToken = jwtTokenUtil.generateToken(loggedUser1Details);

        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(accountRepositoryMock.findAllByUser(user)).thenReturn(accounts);
        when(fixedTermDepositRepositoryMock.findAllByUser(user)).thenReturn(fixedTermDeposits);
    }

    @Test
    void getBalance_NoTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/accounts/balance").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getBalance_WrongTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/accounts/balance").
                        header("Authorization", "Bearer " + userToken + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrent_UserRole_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(obtainBalanceDto(accounts, fixedTermDeposits));
        mockMvc.perform(get("/accounts/balance").
                        header("Authorization", "Bearer " + userToken).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }

    private AccountBalanceDto obtainBalanceDto(List<Account> accounts, List<FixedTermDeposit> fixedTermDeposits){
        Map<Currency, Double> balances = new HashMap<>();
        List<FixedTermDepositDto> depositDtos = new ArrayList<>();
        AccountBalanceDto dto = new AccountBalanceDto();
        dto.setBalances(balances);
        dto.setFixedTermDepositList(depositDtos);

        for (Currency currency : Currency.values())
            balances.put(currency, 0D);

        for (Account account : accounts)
            balances.put(account.getCurrency(), account.getBalance() + balances.get(account.getCurrency()));

        for (FixedTermDeposit fixedTermDeposit : fixedTermDeposits){
            depositDtos.add(fixedTermDepositMapper.fixedTermDepositToFixedTermDepositDto(fixedTermDeposit));
        }
        return dto;
    }

}