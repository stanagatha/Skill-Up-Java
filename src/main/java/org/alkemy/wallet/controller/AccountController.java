package org.alkemy.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping
    @Operation(summary = "Get all accounts",
               description = "Only accessible as an ADMIN. Paginated (initial number: 0).")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Page<AccountDto>> getAll(@RequestParam("page") Integer pageNumber) {
        return ResponseEntity.ok().body(accountService.getAll(pageNumber));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get all accounts owned by provided user ID",
               description = "Only accessible as an ADMIN.")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<AccountDto>> getAllByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(accountService.findAllByUser(userId));
    }

    @PostMapping
    @Operation(summary = "Create a new account for the currently authenticated user",
               description = "Must provide currency type.<br>" +
                             "Multiple accounts with the same currency type is not allowed.<br>" +
                             "Balance amount set to 0.")
    public ResponseEntity<AccountDto> createAccount(@RequestParam Currency currency){
        return ResponseEntity.ok().body(accountService.createAccount(currency));
    }

    @GetMapping("/balance")
    @Operation(summary = "Get balance information from currently authenticated user",
               description = "Show balance information from owned accounts, also fixed-term deposits, both ARS and USD.")
    public ResponseEntity<AccountBalanceDto> getBalance() {
        return ResponseEntity.ok().body(userService.getBalance());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account information from provided ID")
    public ResponseEntity<AccountDto> getById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(accountService.getById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an existing account",
               description = "The account must be from currently authenticated user. " +
                             "Can only modify \"transaction limit\" field.")
    public ResponseEntity<AccountDto> editById(@PathVariable("id") Long id,
                                           @RequestBody Map<String, Double> requestBody){
        return ResponseEntity.ok().body(accountService.editById(id, requestBody.get("transactionLimit")));
    }

}
