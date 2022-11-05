package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fixedDeposit")
public class FixedTermDepositController {
    private final IFixedTermDepositService iFixedTermDepositService;
    @Autowired
    public FixedTermDepositController(IUserService iUserService , IFixedTermDepositService iFixedTermDepositService) {
        this.iFixedTermDepositService = iFixedTermDepositService;
    }
    @PostMapping("")
    public ResponseEntity<FixedTermDepositDto> createFixedDeposit(@RequestBody FixedTermDepositRequestDto deposit){
        FixedTermDepositDto fixedTermDepositDto = iFixedTermDepositService.createDeposit(deposit);
        return ResponseEntity.ok().body(fixedTermDepositDto);
    }

}
