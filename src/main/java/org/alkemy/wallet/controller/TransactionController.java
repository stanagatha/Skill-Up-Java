package org.alkemy.wallet.controller;

import java.util.Map;

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
@RequestMapping("/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("user/{userId}")
    public ResponseEntity<Page<TransactionDto>> getAllByUser(@PathVariable(name = "userId") long userId, @RequestParam("page") Integer pageNumber){
        return ResponseEntity.ok().body(transactionService.getAllByUser(userId, pageNumber));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> edit(@PathVariable(name = "id") long id,
                                               @RequestBody Map<String, String> requestBody){
        return new ResponseEntity<>(transactionService.edit(id, requestBody.get("description")), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> deposit(@RequestBody TransactionRequestDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.DEPOSIT);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionDto));
    }

    @PostMapping("/payment")
    public ResponseEntity<TransactionDto> payment(@RequestBody TransactionRequestDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.PAYMENT);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionDto));
    }

    @PostMapping("/sendArs")
    public ResponseEntity<TransactionDto> sendArs(@RequestBody TransactionSendMoneyDto transactionSendMoneyDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.send(transactionSendMoneyDto, Currency.ARS));
    }

    @PostMapping("/sendUsd")
    public ResponseEntity<TransactionDto> sendUsd(@RequestBody TransactionSendMoneyDto transactionSendMoneyDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.send(transactionSendMoneyDto, Currency.USD));
    }

}
