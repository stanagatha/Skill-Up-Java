package org.alkemy.wallet.repository;

import org.alkemy.wallet.model.FixedTermDeposit;
import org.alkemy.wallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFixedTermDepositRepository extends JpaRepository<FixedTermDeposit, Long> {
    List<FixedTermDeposit> findAllByUser(User user);
}
