package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDto accountToAccountDto(Account account){
        if (account == null)
            return null;

        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setCurrency(account.getCurrency());
        accountDto.setTransactionLimit(account.getTransactionLimit());
        accountDto.setBalance(account.getBalance());
        accountDto.setCreationDate(account.getCreationDate());
        accountDto.setUpdateDate(account.getUpdateDate());
        accountDto.setSoftDelete(account.getSoftDelete());

        return accountDto;

    }

    public Account accountDtoToAccount(AccountDto accountDto){
        if (accountDto == null)
            return null;

        Account account = new Account();

        account.setId(accountDto.getId());
        account.setCurrency(accountDto.getCurrency());
        account.setTransactionLimit(accountDto.getTransactionLimit());
        account.setBalance(accountDto.getBalance());
        account.setCreationDate(accountDto.getCreationDate());
        account.setUpdateDate(accountDto.getUpdateDate());
        account.setSoftDelete(accountDto.getSoftDelete());

        return account;

    }

}
