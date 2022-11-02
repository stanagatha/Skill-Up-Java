package org.alkemy.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FixedTermDepositController {
    @PostMapping("/fixedDeposit")
    public ResponseEntity<Object> createFixedDeposit(@RequestParam double amount){
        return new ResponseEntity<>("fixed deposit created", HttpStatus.CREATED);
    }
}
