package org.alkemy.wallet.service;

import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;

public interface IRoleService {

	Role findByRoleName(RoleName admin);
}
