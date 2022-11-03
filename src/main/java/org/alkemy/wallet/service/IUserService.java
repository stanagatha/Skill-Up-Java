package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.UserDto;

import java.util.List;

public interface IUserService {

    List<UserDto> getAll();

    String deleteById(Long id);

}
