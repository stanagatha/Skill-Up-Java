package org.alkemy.wallet.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

//@WebMvcTest(FixedTermDepositController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FixedDepositTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private IFixedTermDepositService fixedTermDepositService;
  @Autowired
  private ObjectMapper jsonMapper;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @MockBean
  private IUserRepository userRepository;
  private User user;
  private FixedTermDepositRequestDto fixedTermDepositRequestDto = new FixedTermDepositRequestDto();
  private FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();

  @BeforeEach
  private void setUp(){
    Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
    Role adminRole = new Role(2L, RoleName.ADMIN, "ADMIN Role", new Date(), new Date());

    user = new User("user", "test", "userEmail@email.com", "1234", userRole);
    when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

    fixedTermDepositRequestDto.setAmount(1000.0);
    fixedTermDepositRequestDto.setClosingDate(new Date());
    fixedTermDepositRequestDto.setCurrency(Currency.ARS);

    fixedTermDepositDto.setId((long) 1);
    fixedTermDepositDto.setAmount(fixedTermDepositRequestDto.getAmount());
    var depositDuration = ( fixedTermDepositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
    fixedTermDepositDto.setInterest(fixedTermDepositRequestDto.getAmount() * 0.05 * depositDuration);
    fixedTermDepositDto.setCreationDate(new Date());
    fixedTermDepositDto.setClosingDate(fixedTermDepositRequestDto.getClosingDate());

    when(fixedTermDepositService.createDeposit(fixedTermDepositRequestDto)).thenReturn(fixedTermDepositDto);
  }

  @Test
  //@WithMockUser(username = "userEmail@email.com", password = "1234", roles = {"USER"})
  public void fixedDepositShouldCreateDto() throws Exception{
    String userToken = jwtTokenUtil.generateToken(new org.springframework.security.core.userdetails.User("userEmail@email.com", "1234", new ArrayList<>()));
    String jsonResponse = jsonMapper.writeValueAsString(fixedTermDepositDto);
    RequestBuilder request = MockMvcRequestBuilders
      .post("/fixedDeposit")
      .content(jsonMapper.writeValueAsString(fixedTermDepositRequestDto))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "Bearer " + userToken);

    mockMvc.perform(request)
    .andExpect(status().isOk())
    .andExpect(content().json(jsonResponse));
  }

  @Test
  public void create_NoTokenProvided_UnathorizedResponse() throws Exception{
    RequestBuilder request = MockMvcRequestBuilders
      .post("/fixedDeposit")
      .content(jsonMapper.writeValueAsString(fixedTermDepositRequestDto))
      .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request)
    .andExpect(status().isUnauthorized());
  }

}
