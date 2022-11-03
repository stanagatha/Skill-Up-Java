package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.model.TypeTransaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public Transaction transactionDtoToTransaction(TransactionDto transactionDto){
        if (transactionDto == null)
            return null;

        Transaction transaction = new Transaction();
        transaction.setId(transactionDto.getId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTypeTransaction(TypeTransaction.valueOf(transactionDto.getTypeTransaction()));
        transaction.setDescript(transactionDto.getDescript());
        transaction.setTransactionDate(transactionDto.getTransactionDate());

        return transaction;
    }

    public TransactionDto transactionToTransactionDto(Transaction transaction){
        if (transaction == null)
            return null;

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTypeTransaction(transaction.getTypeTransaction().name());
        transactionDto.setDescript(transaction.getDescript());
        transactionDto.setTransactionDate(transaction.getTransactionDate());

        return transactionDto;
    }
}
