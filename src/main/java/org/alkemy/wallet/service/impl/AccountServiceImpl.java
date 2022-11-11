package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.AccountDto;

import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.exception.ForbiddenException;
import org.alkemy.wallet.exception.NotFoundException;

import org.alkemy.wallet.mapper.AccountMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl implements IAccountService {

    private final IAccountRepository accountRepository;

    private final IUserRepository userRepository;

    private final AccountMapper accountMapper;
    private final MessageSource messageSource;

    @Autowired
    public AccountServiceImpl(IAccountRepository accountRepository, IUserRepository userRepository, AccountMapper accountMapper, MessageSource messageSource) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
        this.messageSource = messageSource;
    }

    private String message(String message){
        return messageSource.getMessage(message,null, Locale.US);
    }

    @Override
    public Page<AccountDto> getAll(Integer pageNumber){

        if(pageNumber == null || pageNumber < 0)
            throw new BadRequestException(message("page.invalid.number"));

        Page<Account> accounts = accountRepository.findAll(PageRequest.of(pageNumber,10));

        if((accounts.getTotalPages() - 1) < pageNumber){
            throw new BadRequestException(message("page.wrong-number"));
        }

        return accounts.map(account -> accountMapper.accountToAccountDto(account));

    }

    @Override
    public List<AccountDto> findAllByUser(Long userId) {
        if (userId == null || userId <= 0)
            throw new NotFoundException(message("user.null-id"));

        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            List<Account> accounts = accountRepository.findAllByUser(userOptional.get());
            List<AccountDto> accountsDto = new ArrayList<>();
            if(!accounts.isEmpty()){
                for (Account account: accounts) {
                    AccountDto accountDto = accountMapper.accountToAccountDto(account);
                    accountsDto.add(accountDto);
                }
            }
            return accountsDto;
        } else {
            throw new NotFoundException(messageSource.getMessage("user.invalid-id",new Long[]{userId},Locale.US));
        }
    }

    @Override
    @Transactional
    public AccountDto createAccount(Currency currency) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        return createAccount(user, currency);
    }

    @Override
    @Transactional
    public  AccountDto createAccount(User user, Currency currency){
        List<Account> accounts = accountRepository.findAllByUser(user);
        if (user == null) {
            throw new NotFoundException(message("user.not-found"));
        }
        accounts.forEach(account -> {
            if (account.getCurrency() == currency) {
                throw new ForbiddenException(message("account.already-exist"));
            }
        });
        Account account = new Account();
        account.setBalance(0.0);
        account.setCurrency(currency);
        account.setCreationDate(new Date());
        account.setUpdateDate(new Date());
        account.setSoftDelete(false);
        account.setUser(user);
        double limit = 0;
        if (currency == Currency.ARS) {
            limit = 300000.0;
        } else {
            limit = 1000.0;
        }
        account.setTransactionLimit(limit);
        return accountMapper.accountToAccountDto(accountRepository.save(account));
    }

    @Override
    public AccountDto editById(Long id, Double transactionLimit) {
            if (transactionLimit == null)
                throw new BadRequestException(message("account.mandatory-limit"));

        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty())
            throw new NotFoundException(message("account.not-found"));

        if (account.get().getUser().getId() != loggedUserId)
            throw new ForbiddenException(message("account.not-allow-mod"));

        account.get().setTransactionLimit(transactionLimit);
        account.get().setUpdateDate(new Date());
        return accountMapper.accountToAccountDto(accountRepository.save(account.get()));
    }

    @Override
    public AccountDto getById(Long id) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty())
            throw new NotFoundException(message("account.not-found"));

        if (account.get().getUser().getId() != loggedUserId)
            throw new ForbiddenException(message("account.not-allow-view"));

        return accountMapper.accountToAccountDto(account.get());
    }

}

