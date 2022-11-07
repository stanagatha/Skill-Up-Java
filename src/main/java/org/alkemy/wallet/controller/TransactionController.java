package org.alkemy.wallet.controller;

import java.util.List;
import java.util.Map;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.TypeTransaction;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    private final IUserRepository userRepository;

    @Autowired
    public TransactionController(ITransactionService transactionService,
                                IUserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<TransactionDto>> getAllByUser(@PathVariable(name = "userId") long userId){
        return new ResponseEntity<>(transactionService.getAllByUser(userId), HttpStatus.OK);
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
        return new ResponseEntity<>(transactionService.save(transactionDto), HttpStatus.CREATED);
    }

    @PostMapping("/payment")
    public ResponseEntity<TransactionDto> payment(@RequestBody TransactionRequestDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.PAYMENT);
        return new ResponseEntity<>(transactionService.save(transactionDto), HttpStatus.CREATED);
    }

    @PostMapping("/sendArs")
    public ResponseEntity<TransactionDto> sendArs(@RequestBody TransactionSendMoneyDto transactionSendMoneyDto){
        return ResponseEntity.ok().body(transactionService.send(transactionSendMoneyDto, Currency.ARS));
    }

    @PostMapping("/sendUsd")
    public ResponseEntity<TransactionDto> sendUsd(@RequestBody TransactionSendMoneyDto transactionSendMoneyDto){
        return ResponseEntity.ok().body(transactionService.send(transactionSendMoneyDto, Currency.USD));
    }

}
