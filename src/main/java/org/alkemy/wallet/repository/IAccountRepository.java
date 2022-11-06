package org.alkemy.wallet.repository;

import java.util.List;

import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long>{

  public List<Account> findAllByUser(User user);

  public Account findByCurrencyAndUser(Currency currency, User user);

}
