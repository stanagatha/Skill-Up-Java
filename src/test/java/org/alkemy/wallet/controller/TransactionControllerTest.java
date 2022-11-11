package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
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
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
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
    @Autowired
    private MessageSource messageSource;

    private String user1Token;
    private User user1, user2;
    private Account originAccountArs, originAccountUsd, destinyAccountArs, destinyAccountUsd;
    private TransactionRequestDto depositArsRequestDto;
    private TransactionSendMoneyDto sendMoneyArsRequestDto, sendMoneyUsdRequestDto;

    @BeforeEach
    void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "ADMIN Role", new Date(), new Date());
        user1 = new User(1L, "UserFN", "UserLN", "userEmail@email.com", "1234", userRole, new Date(), new Date(), false);
        user2 = new User(2L,"AdminFN", "AdminLN", "adminEmail@email.com", "5678", adminRole, new Date(), new Date(), false);

        when(userRepositoryMock.findByEmail(user1.getEmail())).thenReturn(user1);
        when(userRepositoryMock.findByEmail(user2.getEmail())).thenReturn(user2);

        UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(
                user1.getEmail(), user1.getPassword(), List.of(new SimpleGrantedAuthority(RoleName.USER.name())));
        user1Token = jwtTokenUtil.generateToken(loggedUserDetails);

        originAccountArs = new Account(1L, Currency.ARS, 2000D, 1000D, user1, new Date(), new Date(), false);
        destinyAccountArs = new Account(2L, Currency.ARS, 5000D, 10300D, user2, new Date(), new Date(), false);
        originAccountUsd = new Account(3L, Currency.USD, 2500D, 1500D, user1, new Date(), new Date(), false);
        destinyAccountUsd = new Account(4L, Currency.USD, 5500D, 10800D, user2, new Date(), new Date(), false);

        when(accountRepository.findById(originAccountArs.getId())).thenReturn(Optional.of(originAccountArs));
        when(accountRepository.findById(originAccountUsd.getId())).thenReturn(Optional.of(originAccountUsd));
        when(accountRepository.findById(destinyAccountArs.getId())).thenReturn(Optional.of(destinyAccountArs));
        when(accountRepository.findById(destinyAccountUsd.getId())).thenReturn(Optional.of(destinyAccountUsd));

        when(accountRepository.findByCurrencyAndUser(originAccountArs.getCurrency(), originAccountArs.getUser())).thenReturn(originAccountArs);
        when(accountRepository.findByCurrencyAndUser(originAccountUsd.getCurrency(), originAccountUsd.getUser())).thenReturn(originAccountUsd);

        depositArsRequestDto = new TransactionRequestDto();
        depositArsRequestDto.setTypeTransaction(TypeTransaction.DEPOSIT);
        depositArsRequestDto.setAmount(100D);
        depositArsRequestDto.setDescription("Deposit");
        depositArsRequestDto.setAccountId(1L);

        sendMoneyArsRequestDto = new TransactionSendMoneyDto();
        sendMoneyArsRequestDto.setAmount(550D);
        sendMoneyArsRequestDto.setDescription("SendArs");
        sendMoneyArsRequestDto.setDestinationAccountId(2L);

        sendMoneyUsdRequestDto = new TransactionSendMoneyDto();
        sendMoneyUsdRequestDto.setAmount(400D);
        sendMoneyUsdRequestDto.setDescription("SendUsd");
        sendMoneyUsdRequestDto.setDestinationAccountId(4L);
    }

    @Test
    void postDeposit_TokenProvided_CreatedResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(depositArsRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(depositArsRequestDto.getAmount()))
                .andExpect(jsonPath("$.accountId").value(depositArsRequestDto.getAccountId()))
                .andExpect(jsonPath("$.description").value(depositArsRequestDto.getDescription()))
                .andExpect(jsonPath("$.typeTransaction").value(TypeTransaction.DEPOSIT.name()));

        assertEquals(originAccountPreBalance + depositArsRequestDto.getAmount(), originAccountArs.getBalance());
    }

    @Test
    void postDeposit_InvalidAmountProvided_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();

        depositArsRequestDto.setAmount(-100D);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(depositArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("amount.invalid",null, Locale.US)));

        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postDeposit_NullDestinationAccount_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();

        depositArsRequestDto.setAccountId(null);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(depositArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.mandatory",null, Locale.US)));

        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postDeposit_NonexistentDestinationAccount_NotFoundResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();

        depositArsRequestDto.setAccountId(10L);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(depositArsRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(messageSource.getMessage("account.not-found",null, Locale.US)));

        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postDeposit_InvalidAccount_ForbiddenResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();

        originAccountArs.setUser(user2);
        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(depositArsRequestDto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(messageSource.getMessage("account.not-allow",null, Locale.US)));

        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postDeposit_InvalidTokenProvided_UnauthorizedResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + user1Token +"fail")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(depositArsRequestDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_UserTokenProvided_OkResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(sendMoneyArsRequestDto.getAmount()))
                .andExpect(jsonPath("$.accountId").value(originAccountArs.getId()))
                .andExpect(jsonPath("$.description").value(sendMoneyArsRequestDto.getDescription()))
                .andExpect(jsonPath("$.typeTransaction").value(TypeTransaction.PAYMENT.name()));

        assertEquals(destinyAccountPreBalance + sendMoneyArsRequestDto.getAmount(), destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance - sendMoneyArsRequestDto.getAmount(), originAccountArs.getBalance());
    }

    @Test
    void postSendArs_InvalidAmountProvided_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        sendMoneyArsRequestDto.setAmount(-100D);
        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("amount.invalid",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_NonexistentDestinationAccount_NotFoundResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        sendMoneyArsRequestDto.setDestinationAccountId(8L);
        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(messageSource.getMessage("not-found.error",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_DestinationAccountEqualsCurrentAccount_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        sendMoneyArsRequestDto.setDestinationAccountId(originAccountArs.getId());
        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.not-same",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_DestinationAccountWithDifferentCurrency_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        destinyAccountArs.setCurrency(Currency.USD);
        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.diff-currency",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_AmountAboveTheLimit_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        sendMoneyArsRequestDto.setAmount(originAccountArs.getTransactionLimit() + 1);
        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("amount.above-limit",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_InsufficientBalance_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        sendMoneyArsRequestDto.setAmount(originAccountPreBalance + 1);
        mockMvc.perform(post("/transactions/sendArs")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.no-enough",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_InvalidTokenProvided_UnauthorizedResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token + "fail")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendArs_NoTokenProvided_UnauthorizedResponse() throws Exception {
        Double originAccountPreBalance = originAccountArs.getBalance();
        Double destinyAccountPreBalance = destinyAccountArs.getBalance();

        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyArsRequestDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(destinyAccountPreBalance, destinyAccountArs.getBalance());
        assertEquals(originAccountPreBalance, originAccountArs.getBalance());
    }

    @Test
    void postSendUsd_UserTokenProvided_OkResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(sendMoneyUsdRequestDto.getAmount()))
                .andExpect(jsonPath("$.accountId").value(originAccountUsd.getId()))
                .andExpect(jsonPath("$.description").value(sendMoneyUsdRequestDto.getDescription()))
                .andExpect(jsonPath("$.typeTransaction").value(TypeTransaction.PAYMENT.name()));

        assertEquals(destinyAccountPreBalance + sendMoneyUsdRequestDto.getAmount(), destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance - sendMoneyUsdRequestDto.getAmount(), originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_InvalidAmountProvided_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        sendMoneyUsdRequestDto.setAmount(-100D);
        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("amount.invalid",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_NonexistentDestinationAccount_NotFoundResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        sendMoneyUsdRequestDto.setDestinationAccountId(8L);
        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(messageSource.getMessage("not-found.error",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_DestinationAccountEqualsCurrentAccount_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        sendMoneyUsdRequestDto.setDestinationAccountId(originAccountUsd.getId());
        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.not-same",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_DestinationAccountWithDifferentCurrency_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        destinyAccountUsd.setCurrency(Currency.ARS);
        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.diff-currency",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_AmountAboveTheLimit_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        sendMoneyUsdRequestDto.setAmount(originAccountUsd.getTransactionLimit() + 1);
        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("amount.above-limit",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_InsufficientBalance_BadRequestResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        sendMoneyUsdRequestDto.setAmount(originAccountPreBalance + 1);
        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(messageSource.getMessage("account.no-enough",null, Locale.US)));

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_InvalidTokenProvided_UnauthorizedResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        mockMvc.perform(post("/transactions/sendUsd")
                        .header("Authorization", "Bearer " + user1Token + "fail")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }

    @Test
    void postSendUsd_NoTokenProvided_UnauthorizedResponse() throws Exception {
        Double originAccountPreBalance = originAccountUsd.getBalance();
        Double destinyAccountPreBalance = destinyAccountUsd.getBalance();

        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(sendMoneyUsdRequestDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(destinyAccountPreBalance, destinyAccountUsd.getBalance());
        assertEquals(originAccountPreBalance, originAccountUsd.getBalance());
    }
}
