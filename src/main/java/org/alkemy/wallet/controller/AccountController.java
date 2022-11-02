package org.alkemy.wallet.controller;

import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private IAccountService iAccountService;
    @PostMapping("/accounts")
    public ResponseEntity<Object> createAccount(@RequestParam Currency currency){
        //Falta parte del loggin para poder continuar
        Account account = new Account();
        account.setCreationDate(new Date());
        account.setCurrency(currency);
        account.setBalance(0);
        iAccountService.saveAccount(account);
        return new ResponseEntity<>("Account created", HttpStatus.CREATED);
    }
}
