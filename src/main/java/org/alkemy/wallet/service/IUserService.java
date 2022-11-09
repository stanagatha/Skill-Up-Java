package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {

    UserDto getCurrent();

    Page<UserDto> getAll(Integer pageNumber);

    String deleteById(Long id);

    User save(User user);

    List<String> getBalance();

}
