package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class AccountController {

    private final IAccountService iAccountService;

    private final IUserService iUserService;

    @Autowired
    public AccountController(IAccountService iAccountService, IUserService iUserService) {
        this.iAccountService = iAccountService;
        this.iUserService = iUserService;
    }

    @Secured("ADMIN")
    @GetMapping("/accounts/{userId}")
    public ResponseEntity<List<AccountDto>> getAllByUserId(@PathVariable("userId") Long userId) {

        return ResponseEntity.ok().body(iAccountService.findAllByUser(userId));

    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> createAccount(@RequestParam Currency currency){
        //Falta parte del loggin para poder continuar
        Account account = new Account();
        account.setCreationDate(new Date());
        account.setCurrency(currency);
        account.setBalance(0d);
        iAccountService.saveAccount(account);
        return new ResponseEntity<>("Account created", HttpStatus.CREATED);
    }

    @GetMapping("/account/balance")
    public ResponseEntity<List<String>> test() {
        return ResponseEntity.ok().body(iUserService.getBalance());
    }

}
