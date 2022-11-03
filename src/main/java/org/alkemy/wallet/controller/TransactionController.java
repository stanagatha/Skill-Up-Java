package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.mapper.TransactionMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.model.TypeTransaction;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final ITransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final IAccountService accountService;

    @Autowired
    public TransactionController(ITransactionService transactionService, TransactionMapper transactionMapper, IAccountService accountService) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.accountService = accountService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionDto transactionDto, @RequestParam("accountId") long accountId){
        Account account = accountService.findById(accountId);
        transactionDto.setTypeTransaction(TypeTransaction.deposit.name());
        Transaction transaction = transactionMapper.transactionDtoToTransaction(transactionDto);
        transaction.setAccountId(account);
        return new ResponseEntity<>(transactionService.save(transaction), HttpStatus.OK);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody TransactionDto transactionDto, @RequestParam("accountId") long accountId){
        Account account = accountService.findById(accountId);
        transactionDto.setTypeTransaction(TypeTransaction.payment.name());
        Transaction transaction = transactionMapper.transactionDtoToTransaction(transactionDto);
        transaction.setAccountId(account);
        return new ResponseEntity<>(transactionService.save(transaction), HttpStatus.OK);
    }

}
