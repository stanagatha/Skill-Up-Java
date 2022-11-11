package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.mapper.FixedTermDepositMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.FixedTermDeposit;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Locale;

@Service
public class FixedTermDepositImpl implements IFixedTermDepositService {

    private final IFixedTermDepositRepository iFixedTermDepositRepository;
    private final IUserRepository iUserRepository;
    private final IAccountRepository iAccountRepository;
    private final FixedTermDepositMapper fixedTermDepositMapper;
    private final MessageSource messageSource;

    @Autowired
    public FixedTermDepositImpl(FixedTermDepositMapper fixedTermDepositMapper, IAccountRepository iAccountRepository, IUserRepository iUserRepository, IFixedTermDepositRepository iFixedTermDepositRepository, MessageSource messageSource) {
        this.iFixedTermDepositRepository = iFixedTermDepositRepository;
        this.iUserRepository = iUserRepository;
        this.iAccountRepository = iAccountRepository;
        this.fixedTermDepositMapper = fixedTermDepositMapper;
        this.messageSource = messageSource;
    }

    private String message(String message){
        return messageSource.getMessage(message,null, Locale.US);
    }

    @Override
    public void saveFixedDeposit(FixedTermDeposit fixedTermDeposit){
        iFixedTermDepositRepository.save(fixedTermDeposit);
    }
    @Override
    @Transactional
    public FixedTermDepositDto createDeposit(FixedTermDepositRequestDto depositRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = iUserRepository.findByEmail(email);
        Account account = iAccountRepository.findByCurrencyAndUser(depositRequestDto.getCurrency(), user);

        if (user == null){
            throw new NotFoundException(message("user.null-id"));
        }
        if(account == null){
            throw new NotFoundException(message("account.not-found"));
        }
        Long depositDuration = ( depositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        if (depositDuration < 30){
            throw new BadRequestException(message("fixed.invalid-time"));
        }
        if(account.getBalance()< depositRequestDto.getAmount() ){
            throw new BadRequestException(message("fixed.no-balance"));
        }
        if (depositRequestDto.getAmount() <= 0){
            throw new BadRequestException(message("amount.invalid"));
        }
        Double interest = depositRequestDto.getAmount() * 0.05 * depositDuration;
        FixedTermDeposit fixedTermDeposit = new FixedTermDeposit();
        fixedTermDeposit.setAccount(account);
        fixedTermDeposit.setAmount(depositRequestDto.getAmount());
        fixedTermDeposit.setCreationDate(new Date());
        fixedTermDeposit.setClosingDate(depositRequestDto.getClosingDate());
        fixedTermDeposit.setInterest(interest);
        fixedTermDeposit.setUser(user);
        account.setBalance(account.getBalance()-depositRequestDto.getAmount());
        iAccountRepository.save(account);
        return  fixedTermDepositMapper.fixedTermDepositToFixedTermDepositDto(iFixedTermDepositRepository.save(fixedTermDeposit));

    }

    @Override
    public FixedTermDepositSimulateDto simulateDeposit(FixedTermDepositRequestDto depositRequestDto) {
        Long depositDuration = ( depositRequestDto.getClosingDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) % 365;
        if (depositDuration < 30){
            throw new BadRequestException(message("fixed.invalid-time"));
        }
        if (depositRequestDto.getAmount() <= 0){
            throw new BadRequestException(message("amount.invalid"));
        }
        Double interest = depositRequestDto.getAmount() * 0.05 * depositDuration;
        FixedTermDepositSimulateDto fixedTermDepositDto = new FixedTermDepositSimulateDto();
        fixedTermDepositDto.setAmount(depositRequestDto.getAmount());
        fixedTermDepositDto.setClosingDate(depositRequestDto.getClosingDate());
        fixedTermDepositDto.setCreationDate(new Date());
        fixedTermDepositDto.setInterest(interest);
        fixedTermDepositDto.setTotalAmount(depositRequestDto.getAmount() + interest);
        return  fixedTermDepositDto;

    }


}
