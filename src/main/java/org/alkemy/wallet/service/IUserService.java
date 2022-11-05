package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.model.User;

import java.util.List;

public interface IUserService {

    UserDto getCurrent();

    List<UserDto> getAll();

    String deleteById(Long id);

    User save(User user);

    List<String> getBalance();

	void createAccounts(User userSaved);

}
