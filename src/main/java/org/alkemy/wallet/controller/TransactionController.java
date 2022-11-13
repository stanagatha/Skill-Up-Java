package org.alkemy.wallet.controller;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.TypeTransaction;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Transactions", description = "TransactionController")
@RequestMapping("/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get all transactions from the currently authenticated user",
            description = "Only accessible as an ADMIN. Must provide own user ID.")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Page<TransactionDto>> getAllByUser(@PathVariable("userId") long userId,
                                                             @RequestParam("page") Integer pageNumber){
        return ResponseEntity.ok().body(transactionService.getAllByUser(userId, pageNumber));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a transaction information")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Edit a transaction information",
            description = "Can only modify \"descript\" field.")
    public ResponseEntity<TransactionDto> edit(@PathVariable(name = "id") long id,
                                               @RequestBody Map<String, String> requestBody){
        return new ResponseEntity<>(transactionService.edit(id, requestBody.get("description")), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Make a deposit",
            description = "Deposit amount must be more than 0.")
    public ResponseEntity<TransactionDto> deposit(@RequestBody TransactionRequestDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.DEPOSIT);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionDto));
    }

    @PostMapping("/payment")
    @Operation(summary = "Make a payment",
            description = "Payment amount must be more than 0.")
    public ResponseEntity<TransactionDto> payment(@RequestBody TransactionRequestDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.PAYMENT);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionDto));
    }

    @PostMapping("/sendArs")
    @Operation(summary = "Send money (ARS) to provided account ID",
            description = "Required information: amount, description, account ID.<br>" +
                    "Destination account must be from another user.")
    public ResponseEntity<TransactionDto> sendArs(@RequestBody TransactionSendMoneyDto transactionSendMoneyDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.send(transactionSendMoneyDto, Currency.ARS));
    }

    @PostMapping("/sendUsd")
    @Operation(summary = "Send money (USD) to provided account ID",
            description = "Required information: amount, description, account ID.<br>" +
                    "Destination account must be from another user.")
    public ResponseEntity<TransactionDto> sendUsd(@RequestBody TransactionSendMoneyDto transactionSendMoneyDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.send(transactionSendMoneyDto, Currency.USD));
    }

}
