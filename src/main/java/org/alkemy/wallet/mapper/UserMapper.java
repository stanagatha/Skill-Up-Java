package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto userToUserDTO(User user){
        if (user == null)
            return null;

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoleName(user.getRole().getRoleName().name());
        userDto.setCreationDate(user.getCreationDate());
        userDto.setUpdateDate(user.getUpdateDate());
        userDto.setSoftDelete(user.isSoftDelete());
        return userDto;
    }

}
