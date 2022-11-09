package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.model.*;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionsDepositTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ITransactionRepository transactionRepository;
    @MockBean
    private IAccountRepository accountRepository;
    @MockBean
    private IUserRepository userRepositoryMock;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String userToken;
    private User admin;
    private Account account;
    private TransactionRequestDto transactionRequestDto;


    @BeforeEach
    public void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "ADMIN Role", new Date(), new Date());
        User user = new User("UserFN", "UserLN", "userEmail@email.com", "1234", userRole);
        admin = new User("AdminFN", "AdminLN", "adminEmail@email.com", "5678", adminRole);
        user.setId(1L);
        admin.setId(2L);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findByEmail(admin.getEmail())).thenReturn(admin);
        UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority(RoleName.USER.name())));
        userToken = jwtTokenUtil.generateToken(loggedUserDetails);

        account = new Account();
        account.setBalance(5000D);
        account.setId(1L);
        account.setUser(user);
        account.setCurrency(Currency.ARS);
        account.setTransactionLimit(2000D);
        account.setCreationDate(new Date());
        account.setSoftDelete(false);
        account.setUpdateDate(new Date());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.findByCurrencyAndUser(account.getCurrency(), account.getUser())).thenReturn(account);

        Account destinyAccount = new Account();
        destinyAccount.setBalance(10300D);
        destinyAccount.setId(2L);
        destinyAccount.setUser(admin);
        destinyAccount.setCurrency(Currency.ARS);
        destinyAccount.setTransactionLimit(5000D);
        destinyAccount.setCreationDate(new Date());
        destinyAccount.setSoftDelete(false);
        destinyAccount.setUpdateDate(new Date());
        when(accountRepository.findById(destinyAccount.getId())).thenReturn(Optional.of(destinyAccount));

        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setTypeTransaction(TypeTransaction.DEPOSIT);
        transactionRequestDto.setAmount(100D);
        transactionRequestDto.setDescription("String");
        transactionRequestDto.setAccountId(1L);

        jsonMapper = new ObjectMapper();
    }

    @Test
    void post_TokenProvided_CreatedResponse() throws Exception {
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100D))
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.description").value("String"))
                .andExpect(jsonPath("$.typeTransaction").value(TypeTransaction.DEPOSIT.name()));
    }

    @Test
    void post_InvalidAmountProvided_BadRequestResponse() throws Exception {
        transactionRequestDto.setAmount(-100D);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount must be greater than 0"));
    }

    @Test
    void post_DestinationAccountNull_BadRequestResponse() throws Exception {
        transactionRequestDto.setAccountId(null);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Destination account id is mandatory"));
    }

    @Test
    void post_DestinationAccountNotFound_NotFoundResponse() throws Exception {
        transactionRequestDto.setAccountId(10L);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    void post_InvalidAccount_ForbiddenResponse() throws Exception {
        account.setUser(admin);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not allow to register transactions in other accounts than yours"));
    }

    @Test
    void post_InvalidTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + userToken+"fail")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isUnauthorized());
    }
}
