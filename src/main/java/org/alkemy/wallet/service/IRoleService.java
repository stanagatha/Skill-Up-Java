package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.RoleDto;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;

public interface IRoleService {

	RoleDto findByRoleName(RoleName admin);

}
