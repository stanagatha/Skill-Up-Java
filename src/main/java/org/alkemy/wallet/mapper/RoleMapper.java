package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.RoleDto;
import org.alkemy.wallet.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role roleDtoToRole(RoleDto roleDto){
        if ( roleDto == null)
            return null;

        Role role = new Role();
        role.setId(roleDto.getId());
        role.setRoleName(roleDto.getRoleName());
        role.setDescription(roleDto.getDescription());
        role.setCreationDate(roleDto.getCreationDate());
        role.setUpdateDate(roleDto.getUpdateDate());

        return role;
    }

    public RoleDto roleToRoleDTO(Role role){
        if ( role == null )
            return null;

        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setRoleName(role.getRoleName());
        roleDto.setDescription(role.getDescription());
        roleDto.setCreationDate(role.getCreationDate());
        roleDto.setUpdateDate(role.getUpdateDate());

        return roleDto;
    }
}
