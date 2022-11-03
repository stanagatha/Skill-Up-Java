package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private IAccountRepository iAccountRepository;
    @Override
    public void saveAccount(Account account){
        iAccountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(long accountId) {
        return iAccountRepository.findById(accountId).orElse(null);
    }

}
