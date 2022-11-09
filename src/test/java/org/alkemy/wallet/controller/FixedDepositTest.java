package org.alkemy.wallet.controller;

import org.alkemy.wallet.service.IFixedTermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FixedTermDepositController.class)
public class FixedDepositTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private IFixedTermDepositService fixedTermDepositService;

}
