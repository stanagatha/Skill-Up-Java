package org.alkemy.wallet.mapper;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.service.impl.AccountServiceImpl;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import lombok.NoArgsConstructor;

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
