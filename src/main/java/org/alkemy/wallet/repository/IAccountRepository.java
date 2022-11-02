package org.alkemy.wallet.repository;

import org.alkemy.wallet.model.Account;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IAccountRepository extends PagingAndSortingRepository<Account, Long>{

}
