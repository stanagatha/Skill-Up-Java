package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IAccountService {

    Account findById(long accountId);

    List<AccountDto> findAllByUser(Long userId);

    AccountDto createAccount(Currency currency);

    AccountDto createAccount(User user, Currency currency);
}
