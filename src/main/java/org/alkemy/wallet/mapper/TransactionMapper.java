package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper{

    /*
    public Transaction transactionDtoToTransaction(TransactionDto transactionDto){
        if (transactionDto == null)
            return null;

        Transaction transaction = new Transaction();
        transaction.setId(transactionDto.getId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescript(transaction.getDescript());
        transaction.setTypeTransaction(TypeTransaction.valueOf(transactionDto.getTypeTransaction()));
        // TODO
        transaction.setTransactionDate(transactionDto.getTransactionDate());

        return transaction;
    }
    */

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

/*

@Mapper(componentModel = "spring")
@NoArgsConstructor
public abstract class TransactionMapper {

    private AccountServiceImpl accountService;

    public abstract Transaction transactionDtoToTransaction(TransactionDto transactionDto);

    protected Account toAccount(long accountId){
        return accountService.findById(accountId);
    }

    protected long toAccountDto(Account account){
        return account.getId();
    }

    @InheritInverseConfiguration
    public abstract TransactionDto transactionToTransactionDto(Transaction transaction);
    public abstract List<TransactionDto> toTransactionsDto(List<Transaction> transaction);
    public abstract List<Transaction> toTransactions(List<TransactionDto> transactionsDto);

}

 */