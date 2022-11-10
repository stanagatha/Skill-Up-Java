package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.mapper.AccountMapper;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.*;
import org.alkemy.wallet.repository.IAccountRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private UserMapper userMapper;
    @Autowired private ObjectMapper jsonMapper;
    @MockBean private IUserRepository userRepositoryMock;
    @MockBean private IAccountRepository accountRepositoryMock;

    // Test fixture
    private String user1Token;
    private User user1;
    private List<Account> accounts;

    @BeforeEach
    private void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        user1 = new User(1L, "User1FN", "User1LN", "user1Email@email.com", "1234", userRole, new Date(), new Date(), false);

        Account account1 = new Account(1L, Currency.ARS, 1500D, 20000D, user1, new Date(), new Date(), false);
        Account account2 = new Account(2L, Currency.ARS, 1500D, 35000D, user1, new Date(), new Date(), false);
        Account account3 = new Account(3L, Currency.ARS, 1500D, 750D, user1, new Date(), new Date(), false);
        Account account4 = new Account(4L, Currency.USD, 1500D, 300D, user1, new Date(), new Date(), false);
        Account account5 = new Account(5L, Currency.USD, 1500D, 800D, user1, new Date(), new Date(), false);
        accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        accounts.add(account4);
        accounts.add(account5);

        UserDetails loggedUser1Details = new org.springframework.security.core.userdetails.User(user1.getEmail(), user1.getPassword(), new ArrayList<>());
        user1Token = jwtTokenUtil.generateToken(loggedUser1Details);

        when(userRepositoryMock.findByEmail(user1.getEmail())).thenReturn(user1);
        when(userRepositoryMock.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(accountRepositoryMock.findAllByUser(user1)).thenReturn(accounts);
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
                        header("Authorization", "Bearer " + user1Token + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrent_UserRole_OkResponse() throws Exception {
        mockMvc.perform(get("/accounts/balance").
                        header("Authorization", "Bearer " + user1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

}