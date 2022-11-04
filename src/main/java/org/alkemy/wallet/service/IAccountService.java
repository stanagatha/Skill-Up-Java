package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;

import java.util.List;

public interface IAccountService {

    Account save(Account account);

    Account getById(long accountId);

    Account getByCurrencyAndUser(Currency currency, User user);
    List<AccountDto> findAllByUser(Long userId);


}
