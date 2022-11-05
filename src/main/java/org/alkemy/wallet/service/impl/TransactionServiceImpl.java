package org.alkemy.wallet.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.mapper.TransactionMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final ITransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final IAccountService accountService;

    @Autowired
    public TransactionServiceImpl(ITransactionRepository transactionRepository, TransactionMapper transactionMapper, IAccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public TransactionDto save(TransactionDto transactionDto) {
        if(transactionDto.getAmount() <=0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        transactionDto.setTransactionDate(new Date(Calendar.getInstance().getTimeInMillis()));
        Account account = accountService.findById(transactionDto.getAccountId());
        Transaction transaction = transactionMapper.transactionDtoToTransaction(transactionDto);
        transaction.setAccountId(account);
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDto getAll(long userId) {
        return (TransactionDto) null /* userRepository.findById(userId).map(user -> {
            List<Account> accounts= accountRepository.findAllByUser(user);
            List<Transaction> transactions= new ArrayList<>();
            accounts.forEach(acc -> {
                transactions.addAll(transactionRepository.findAllByAccountId(acc));
            });
            System.out.println("Get all, no users");
            transactions.forEach(System.out::println);;
            //implemente mapper;
            return null;
        }).orElse(null)*/;
    }

}