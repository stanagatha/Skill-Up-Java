package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.AccountDto;
import org.alkemy.wallet.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

  public abstract AccountDto toDto(Account account);
  public abstract Account toEntity(AccountDto accountDto);

}
