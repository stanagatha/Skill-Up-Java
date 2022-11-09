package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.exception.ForbiddenException;
import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.mapper.UserMapper;

import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
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
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final IAccountService accountService;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, UserMapper userMapper, IAccountService accountService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.accountService = accountService;
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
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(userEmail).getId();

        List<AccountDto> accounts = accountService.findAllByUser(userId);
        List<String> balances = new ArrayList<>();

        for (AccountDto account : accounts) {
            balances.add(account.getCurrency() + ": " + account.getBalance());
        }

        return balances;
    }

}
