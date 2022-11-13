package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper{

    public TransactionDto transactionToTransactionDto(Transaction transaction){
        if (transaction == null)
            return null;

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setDescription(transaction.getDescript());
        transactionDto.setTypeTransaction(transaction.getTypeTransaction());
        transactionDto.setAccountId(transaction.getAccount().getId());
        transactionDto.setTransactionDate(transaction.getTransactionDate());

        return transactionDto;
    }
}