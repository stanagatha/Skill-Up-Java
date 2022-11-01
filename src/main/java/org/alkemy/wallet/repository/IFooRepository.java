package org.alkemy.wallet.repository;

import org.alkemy.wallet.model.Foo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFooRepository extends JpaRepository<Foo, Long> {
}
