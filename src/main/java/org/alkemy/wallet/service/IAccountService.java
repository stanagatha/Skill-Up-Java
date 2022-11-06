package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;

import org.alkemy.wallet.model.User;

import java.util.List;

public interface IAccountService {

    public void saveAccount(Account account);

    public Account findById(long accountId);

    List<AccountDto> findAllByUser(Long userId);

    AccountDto createAccount(Currency currency);

}
