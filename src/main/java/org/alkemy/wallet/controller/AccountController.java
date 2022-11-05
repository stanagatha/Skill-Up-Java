package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final IAccountService iAccountService;

    private final IUserService iUserService;

    @Autowired
    public AccountController(IAccountService iAccountService, IUserService iUserService) {
        this.iAccountService = iAccountService;
        this.iUserService = iUserService;
    }

    @Secured("ADMIN")
    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountDto>> getAllByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(iAccountService.findAllByUser(userId));
    }

    public ResponseEntity<AccountDto> createAccount(@RequestParam Currency currency){
        AccountDto account = iAccountService.createAccount(currency);
        return ResponseEntity.ok().body(account);
    }

    @GetMapping("/balance")
    public ResponseEntity<List<String>> getBalance() {
        return ResponseEntity.ok().body(iUserService.getBalance());
    }

}
