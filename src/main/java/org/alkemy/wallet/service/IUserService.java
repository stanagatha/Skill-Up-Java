package org.alkemy.wallet.service;


import org.alkemy.wallet.dto.UserDto;

import java.util.List;

import org.alkemy.wallet.model.User;


public interface IUserService {

    List<UserDto> getAll();

    String deleteById(Long id);

    User save(User user);

    List<String> getBalance();

}
