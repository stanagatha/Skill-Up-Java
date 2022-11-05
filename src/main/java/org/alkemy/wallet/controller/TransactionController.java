package org.alkemy.wallet.controller;

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
    public ResponseEntity<?> getAll(@PathVariable(name = "user_id") long userId){
        try {
            return new ResponseEntity<>(transactionService.getAllByUser(userId), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("ERROR ON TRANSACTION CONTROLLER, GENERAL EXCEPTION");
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.DEPOSIT.name());
        return new ResponseEntity<>(transactionService.save(transactionDto), HttpStatus.CREATED);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody TransactionDto transactionDto){
        transactionDto.setTypeTransaction(TypeTransaction.PAYMENT.name());
        return new ResponseEntity<>(transactionService.save(transactionDto), HttpStatus.CREATED);
    }

}
