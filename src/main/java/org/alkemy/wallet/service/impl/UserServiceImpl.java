package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.*;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.exception.ForbiddenException;
import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.mapper.FixedTermDepositMapper;
import org.alkemy.wallet.mapper.UserMapper;

import org.alkemy.wallet.model.*;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IAccountRepository accountRepository;
    private final IFixedTermDepositRepository fixedTermDepositRepository;
    private final UserMapper userMapper;
    private final FixedTermDepositMapper fixedTermDepositMapper;
    private final MessageSource messageSource;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository,
                           IAccountRepository accountRepository,
                           IFixedTermDepositRepository fixedTermDepositRepository,
                           UserMapper userMapper,
                           FixedTermDepositMapper fixedTermDepositMapper, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.fixedTermDepositRepository = fixedTermDepositRepository;
        this.userMapper = userMapper;
        this.fixedTermDepositMapper = fixedTermDepositMapper;
        this.messageSource = messageSource;
    }

    private String message(String message){
        return messageSource.getMessage(message,null,Locale.US);
    }

    @Override
    public UserDto getCurrent() {
        return userMapper.userToUserDTO(
                userRepository.findByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @Override
    public Page<UserDto> getAll(Integer pageNumber) {

        if(pageNumber == null || pageNumber < 0)
            throw new BadRequestException(message("page.invalid.number"));

        Page<User> users = userRepository.findAll(PageRequest.of(pageNumber,10));

        if((users.getTotalPages() - 1) < pageNumber){
            throw new BadRequestException(message("page.wrong-number"));
        }

        return users.map(user -> userMapper.userToUserDTO(user));

    }

    @Override
    @Transactional
    public String deleteById(Long id) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedUser = userRepository.findByEmail(loggedUserEmail);
        Long loggedUserId = loggedUser.getId();
        Role loggedUserRole = loggedUser.getRole();

        if (loggedUserId != id && loggedUserRole.getRoleName() != RoleName.ADMIN)
            throw new ForbiddenException(message("user.not-allow-delete"));

        Optional<User> toDeleteUser = userRepository.findById(id);
        if (toDeleteUser.isEmpty())
            throw new NotFoundException(messageSource.getMessage("user.invalid-id",new Long[]{id},Locale.US));

        if (toDeleteUser.get().getSoftDelete())
            throw new BadRequestException(message("user.already-deleted"));

        toDeleteUser.get().setSoftDelete(true);
        return messageSource.getMessage("user.deleted",new Long[]{id},Locale.US);
    }

    @Override
    public User save(User user) {
        User existUser = userRepository.findByEmail(user.getEmail());

        if(existUser!=null) {
            throw new BadRequestException(message("user.email-exist"));
        }

        return userRepository.save(user);
    }

    @Override
    public AccountBalanceDto getBalance() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail);

        List<Account> accounts = accountRepository.findAllByUser(user);
        List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositRepository.findAllByUser(user);

        AccountBalanceDto balanceDto = new AccountBalanceDto();

        HashMap<Currency, Double> balance = new HashMap<>();
        for (Currency currency : Currency.values()) {
            balance.put(currency, 0D);
        }
        for (Account account : accounts) {
            Double oldBalanceAmount = balance.get(account.getCurrency());
            balance.put(account.getCurrency(), oldBalanceAmount + account.getBalance());
        }

        List<FixedTermDepositDto> fixedTermDepositDtos = new ArrayList<>();
        for (FixedTermDeposit fixedTermDeposit : fixedTermDeposits) {
            fixedTermDepositDtos.add(fixedTermDepositMapper.fixedTermDepositToFixedTermDepositDto(fixedTermDeposit));
        }

        balanceDto.setBalances(balance);
        balanceDto.setFixedTermDepositList(fixedTermDepositDtos);

        return balanceDto;
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        if(userUpdateDto.firstName == null || userUpdateDto.firstName.isEmpty())
            throw new BadRequestException(message("user.mandatory-name"));
        if(userUpdateDto.lastName == null || userUpdateDto.lastName.isEmpty())
            throw new BadRequestException(message("user.mandatory-lastname"));

        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new NotFoundException(messageSource.getMessage("user.invalid-id",new Long[]{id},Locale.US));

        if (loggedUserId != id)
            throw new ForbiddenException(message("user.not-allow-view"));

        user.get().setFirstName(userUpdateDto.firstName);
        user.get().setLastName(userUpdateDto.lastName);
        user.get().setUpdateDate(new Date());
        return userMapper.userToUserDTO(userRepository.save(user.get()));
    }

    @Override
    public UserDto getById(Long id) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new NotFoundException(messageSource.getMessage("user.invalid-id",new Long[]{id},Locale.US));

        if (loggedUserId != id)
            throw new ForbiddenException(message("user.not-allow-view"));

        return userMapper.userToUserDTO(user.get());
    }

}
