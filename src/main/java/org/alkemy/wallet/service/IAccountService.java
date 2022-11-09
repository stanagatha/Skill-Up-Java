package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;

import java.util.List;

public interface IAccountService {

    List<AccountDto> findAllByUser(Long userId);

    AccountDto createAccount(Currency currency);

    AccountDto createAccount(User user, Currency currency);

    AccountDto edit(Long id, Double transactionLimit);
}
