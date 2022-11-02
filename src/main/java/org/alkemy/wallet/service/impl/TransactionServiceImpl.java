package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements ITransactionService {
    private final ITransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
