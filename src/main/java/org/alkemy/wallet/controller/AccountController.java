package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private IAccountService iAccountService;

    @Autowired
    public AccountController(IAccountService iAccountService) {
        this.iAccountService = iAccountService;
    }


    //@Secured("ADMIN")
    @GetMapping("/{userId}")
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

}
