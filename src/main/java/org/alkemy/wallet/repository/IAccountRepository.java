package org.alkemy.wallet.repository;

import org.alkemy.wallet.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long>{
}
