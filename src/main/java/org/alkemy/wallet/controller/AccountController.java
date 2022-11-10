package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.AccountBalanceDto;
import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAll(@RequestParam("page") Integer pageNumber) {
        return ResponseEntity.ok().body(accountService.getAll(pageNumber));
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
    public ResponseEntity<AccountBalanceDto> getBalance() {
        return ResponseEntity.ok().body(userService.getBalance());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok().body(accountService.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountDto> editById(@PathVariable(name = "id") Long id,
                                           @RequestBody Map<String, Double> requestBody){
        return ResponseEntity.ok().body(accountService.editById(id, requestBody.get("transactionLimit")));
    }
}
