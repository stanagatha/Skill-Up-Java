package org.alkemy.wallet.repository;

import java.util.List;

import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {

  public List<Transaction> findAllByAccount(Account account);

}
