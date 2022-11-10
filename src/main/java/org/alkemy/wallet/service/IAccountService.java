package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAccountService {

    Page<AccountDto> getAll(Integer pageNumber);

    List<AccountDto> findAllByUser(Long userId);

    AccountDto createAccount(Currency currency);

    AccountDto createAccount(User user, Currency currency);

    AccountDto editById(Long id, Double transactionLimit);

    AccountDto getById(Long id);
}
