package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.mapper.AccountMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {

    private final IAccountRepository iAccountRepository;

    private final IUserRepository userRepository;

    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(IAccountRepository iAccountRepository, IUserRepository userRepository, AccountMapper accountMapper) {
        this.iAccountRepository = iAccountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public void saveAccount(Account account){
        iAccountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(long accountId) {
        return iAccountRepository.findById(accountId).orElse(null);
    }

    @Override
    public List<AccountDto> findAllByUser(Long userId) {

        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()){

            List<Account> accounts = iAccountRepository.findAllByUser(userOptional.get());

            List<AccountDto> accountsDto = new ArrayList<>();

            if(!accounts.isEmpty()){

                for (Account account: accounts) {

                    AccountDto accountDto = accountMapper.accountToAccountDto(account);

                    accountsDto.add(accountDto);

                }

            }

            return accountsDto;

        }

        return null;

    }

}
