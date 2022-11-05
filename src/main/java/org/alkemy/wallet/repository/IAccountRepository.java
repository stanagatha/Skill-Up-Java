package org.alkemy.wallet.repository;

import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long>{

    List<Account> findAllByUser(User user);

    Account findByCurrencyAndUser(Currency currency, User user);
}
