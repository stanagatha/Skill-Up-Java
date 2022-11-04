package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.repository.IRoleRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IRoleService;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements IRoleService {
	
	@Autowired
	IRoleRepository roleRepository;
	
	public Role findByRoleName(RoleName roleName) {
		return roleRepository.findByRoleName(roleName);		
	}

}
