package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.dto.UserUpdateDto;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.exception.ForbiddenException;
import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.mapper.UserMapper;

import org.alkemy.wallet.model.*;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final IAccountService accountService;
    private final IFixedTermDepositRepository fixedTermDepositRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository,
                           UserMapper userMapper,
                           IAccountService accountService,
                           IFixedTermDepositRepository fixedTermDepositRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.accountService = accountService;
        this.fixedTermDepositRepository = fixedTermDepositRepository;
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
            throw new BadRequestException("The page number is invalid.");

        Page<User> users = userRepository.findAll(PageRequest.of(pageNumber,10));

        if((users.getTotalPages() - 1) < pageNumber){
            throw new BadRequestException("The page number is greater than the total number of pages.");
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
            throw new ForbiddenException("You are not allow to delete other users different than you.");

        Optional<User> toDeleteUser = userRepository.findById(id);
        if (toDeleteUser.isEmpty())
            throw new NotFoundException("No user with id: " + id);

        if (toDeleteUser.get().getSoftDelete())
            throw new BadRequestException("The user is already deleted.");

        toDeleteUser.get().setSoftDelete(true);
        return "User " + id + " successfully deleted.";
    }

    @Override
    public User save(User user) {
        User existUser = userRepository.findByEmail(user.getEmail());

        if(existUser!=null) {
            throw new BadRequestException("Email already exist");
        }

        return userRepository.save(user);
    }

    @Override
    public List<String> getBalance() {
        String noBalanceFound = "No balance data found for this user";

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(userEmail).getId();

        List<AccountDto> accounts = accountService.findAllByUser(userId);
        List<String> balances = new ArrayList<>();

        for (AccountDto account : accounts) {
            balances.add(account.getCurrency() + ": " + account.getBalance());
        }

        if (balances.isEmpty())
            balances.add(noBalanceFound);

        List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositRepository.findAll();

        for (FixedTermDeposit fixedTermDeposit : fixedTermDeposits) {
            if (fixedTermDeposit.getAccount().getId().equals(userId)) {
                balances.add("Fixed term deposit: " + fixedTermDeposit.getAmount() +
                        " | Interest: " + fixedTermDeposit.getInterest());
            }
        }

        if (balances.contains(noBalanceFound))
            throw new NotFoundException("No balance nor fixed term deposit data found for this user");

        return balances;
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        if(userUpdateDto.firstName == null)
            throw new BadRequestException("firstName is mandatory");
        if(userUpdateDto.lastName == null)
            throw new BadRequestException("lastName is mandatory");

        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new NotFoundException("No user with id: " + id);

        if (loggedUserId != id)
            throw new ForbiddenException("You are not allowed to view this user");

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
            throw new NotFoundException("No user with id: " + id);

        if (loggedUserId != id)
            throw new ForbiddenException("You are not allowed to view this user");

        return userMapper.userToUserDTO(user.get());
    }

}
