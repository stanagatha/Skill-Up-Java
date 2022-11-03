package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final ITransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        if(transaction.getAmount() <=0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if(transaction.getTransactionDate() == null){
            throw new IllegalArgumentException("Date must not be null");
        }
        return transactionRepository.save(transaction);
    }

}
