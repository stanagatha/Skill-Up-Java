package org.alkemy.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Fixed Term Deposits", description = "FixedTermDepositController")
@RequestMapping("/fixedDeposit")
public class FixedTermDepositController {

    private final IFixedTermDepositService fixedTermDepositService;

    @Autowired
    public FixedTermDepositController(IFixedTermDepositService fixedTermDepositService) {
        this.fixedTermDepositService = fixedTermDepositService;
    }

    @PostMapping
    @Operation(summary = "Create a term deposit",
            description = "0.5% interest rate per day.<br>" +
                    "Closing date must be less than 30 days.")
    public ResponseEntity<FixedTermDepositDto> createFixedDeposit(@RequestBody FixedTermDepositRequestDto deposit){
        return ResponseEntity.status(HttpStatus.CREATED).body(fixedTermDepositService.createDeposit(deposit));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simulate a term deposit",
            description = "0.5% interest rate per day.<br>" +
                    "Shows its creation and closing dates, amount invested, interest earned, total amount to collect.<br>" +
                    "This is a visual simulation and its data won't be stored. ")
    public ResponseEntity<FixedTermDepositSimulateDto> simulateFixedTermDeposit(@RequestBody FixedTermDepositRequestDto depositRequestDto){
        return ResponseEntity.ok().body(fixedTermDepositService.simulateDeposit(depositRequestDto));
    }

}
