package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fixedDeposit")
public class FixedTermDepositController {

    private final IFixedTermDepositService fixedTermDepositService;

    @Autowired
    public FixedTermDepositController(IFixedTermDepositService fixedTermDepositService) {
        this.fixedTermDepositService = fixedTermDepositService;
    }

    @PostMapping
    public ResponseEntity<FixedTermDepositDto> createFixedDeposit(@RequestBody FixedTermDepositRequestDto deposit){
        return ResponseEntity.ok().body(fixedTermDepositService.createDeposit(deposit));
    }
    @GetMapping("/simulate")
    public ResponseEntity<FixedTermDepositSimulateDto> simulateFixedTermDeposit(@RequestBody FixedTermDepositRequestDto depositRequestDto){
        return ResponseEntity.ok().body(fixedTermDepositService.simulateDeposit(depositRequestDto));
    }
}
