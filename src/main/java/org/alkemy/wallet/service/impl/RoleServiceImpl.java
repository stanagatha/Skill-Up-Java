package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.dto.RoleDto;
import org.alkemy.wallet.mapper.RoleMapper;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.repository.IRoleRepository;
import org.alkemy.wallet.service.IRoleService;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements IRoleService {

	private final IRoleRepository roleRepository;
	private final RoleMapper roleMapper;

	@Autowired
	public RoleServiceImpl(IRoleRepository roleRepository, RoleMapper roleMapper) {
		this.roleRepository = roleRepository;
		this.roleMapper = roleMapper;
	}

	public RoleDto findByRoleName(RoleName roleName) {
		return roleMapper.roleToRoleDTO(roleRepository.findByRoleName(roleName));
	}

}
