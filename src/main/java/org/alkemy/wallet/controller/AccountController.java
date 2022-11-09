package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.RoleName;
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

    private final IAccountService accountService;

    private final IUserService userService;

    @Autowired
    public AccountController(IAccountService accountService, IUserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> getAllByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(accountService.findAllByUser(userId));
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestParam Currency currency){
        return ResponseEntity.ok().body(accountService.createAccount(currency));
    }

    @GetMapping("/balance")
    public ResponseEntity<List<String>> getBalance() {
        return ResponseEntity.ok().body(userService.getBalance());
    }

}
