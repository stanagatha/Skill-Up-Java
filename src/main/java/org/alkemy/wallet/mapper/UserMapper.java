package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.dto.UserRegisterDto;
import org.alkemy.wallet.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserMapper {

    public User userRegisterDtoToUser(UserRegisterDto userDto){
        if (userDto == null)
            return null;

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // TODO : HASH SOMETHING HERE
        user.setPassword(userDto.getPassword());
        user.setSoftDelete(false);
        user.setCreationDate(new Date());
        user.setUpdateDate(new Date());

        return user;
    }

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
        userDto.setSoftDelete(user.getSoftDelete());

        return userDto;
    }

}
