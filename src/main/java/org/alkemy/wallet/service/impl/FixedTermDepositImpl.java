package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.mapper.FixedTermDepositMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.FixedTermDeposit;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FixedTermDepositImpl implements IFixedTermDepositService {

    private final IFixedTermDepositRepository iFixedTermDepositRepository;
    private final IUserService iUserService;
    private final IAccountService iAccountService;
    private final FixedTermDepositMapper fixedTermDepositMapper;

    @Autowired
    public FixedTermDepositImpl(FixedTermDepositMapper fixedTermDepositMapper, IAccountService iAccountService, IUserService iUserService, IFixedTermDepositRepository iFixedTermDepositRepository) {
        this.iFixedTermDepositRepository = iFixedTermDepositRepository;
        this.iUserService = iUserService;
        this.iAccountService = iAccountService;
        this.fixedTermDepositMapper = fixedTermDepositMapper;
    }

    @Override
    public void saveFixedDeposit(FixedTermDeposit fixedTermDeposit){
        iFixedTermDepositRepository.save(fixedTermDeposit);
    }
    @Override
    public FixedTermDepositDto createDeposit(FixedTermDepositRequestDto depositRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = iUserService.findUserByEmail(email);
        Account account = iAccountService.findByCurrencyAndUser(depositRequestDto.getCurrency(), user);

        if (user == null || account == null){
            throw new NotFoundException("Not exist");
        }

        long depositDuration = ( depositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        System.out.println(depositDuration);
        if (depositDuration < 30){
            throw new BadRequestException("Time cannot be minor of 30");
        }
        if(account.getBalance()< depositRequestDto.getAmount() ){
            throw new BadRequestException("Insufficient balance to carry out the operation");
        }
        Double interest = depositRequestDto.getAmount() * 0.05 * depositDuration;
        FixedTermDeposit fixedTermDeposit = new FixedTermDeposit();
        fixedTermDeposit.setAccountId(account);
        fixedTermDeposit.setAmount(depositRequestDto.getAmount());
        fixedTermDeposit.setCreationDate(new Date());
        fixedTermDeposit.setClosingDate(depositRequestDto.getClosingDate());
        fixedTermDeposit.setInterest(interest);
        fixedTermDeposit.setUserId(user);
        account.setBalance(account.getBalance()-depositRequestDto.getAmount());
        iAccountService.saveAccount(account);
        return  fixedTermDepositMapper.fixedTermDepositToFixedTermDepositDto(iFixedTermDepositRepository.save(fixedTermDeposit));


    }


}
