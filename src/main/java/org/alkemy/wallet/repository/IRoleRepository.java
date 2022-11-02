package org.alkemy.wallet.repository;

import org.alkemy.wallet.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<Role, Long> {
}
