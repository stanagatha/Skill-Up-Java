package org.alkemy.wallet.controller;

import java.util.List;
import java.util.Map;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.TypeTransaction;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<TransactionDto>> getAllByUser(@PathVariable(name = "user_id") long userId){
        return new ResponseEntity<>(transactionService.getAllByUser(userId), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> edit(@PathVariable(name = "id") long id, @RequestBody Map<String, String> requestBody){
        return new ResponseEntity<TransactionDto>(transactionService.edit(id, id, requestBody.get("description")), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> deposit(@RequestBody TransactionDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.DEPOSIT.name());
        return new ResponseEntity<>(transactionService.save(transactionDto), HttpStatus.CREATED);
    }

    @PostMapping("/payment")
    public ResponseEntity<TransactionDto> payment(@RequestBody TransactionDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.PAYMENT.name());
        return new ResponseEntity<>(transactionService.save(transactionDto), HttpStatus.CREATED);
    }

}
