package org.alkemy.wallet.service;

public interface IUserService {

    List<UserDto> getAll();

    String deleteById(Long id);

}
