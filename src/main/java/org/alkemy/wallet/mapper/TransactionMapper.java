package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public Transaction transactionDtoToTransaction(TransactionDto transactionDto){
        if (transactionDto == null)
            return null;

        Transaction transaction = new Transaction();
        transaction.setId(transactionDto.getId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType_transaction(transactionDto.getType_transaction());
        transaction.setDescrip(transactionDto.getDescrip());
        transaction.setTransactionDate(transactionDto.getTransactionDate());

        return transaction;
    }

    public TransactionDto transactionToTransactionDto(Transaction transaction){
        if (transaction == null)
            return null;

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transactionDto.getId());
        transactionDto.setAmount(transactionDto.getAmount());
        transaction.setType_transaction(transactionDto.getType_transaction());
        transactionDto.setDescrip(transactionDto.getDescrip());
        transaction.setTransactionDate(transactionDto.getTransactionDate());

        return transactionDto;
    }
}
