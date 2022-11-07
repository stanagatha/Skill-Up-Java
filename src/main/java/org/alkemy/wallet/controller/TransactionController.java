package org.alkemy.wallet.controller;

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
    public ResponseEntity<?> getAllByUser(@PathVariable(name = "user_id") long userId){
        try {
            return new ResponseEntity<>(transactionService.getAllByUser(userId), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
<<<<<<< HEAD
            System.out.println("ERROR ON TRANSACTION CONTROLLER, GENERAL EXCEPTION");
            System.out.println(e.getMessage());
=======
>>>>>>> develop
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable(name = "id") long id, @RequestBody Map<String, String> requestBody){
        try {
            return new ResponseEntity<TransactionDto>(transactionService.edit(id, id, requestBody.get("description")), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException){
            System.out.println("Error IAE edit method in transaction controller:\n" + illegalArgumentException.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("General error in edit method on transaction controller:\n" + e.getMessage());
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
