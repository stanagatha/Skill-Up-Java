package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.exception.CustomException;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }    
	
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().
                map(user -> userMapper.userToUserDTO(user)).
                collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteById(Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // TODO : Get the correct loggedUserId based on some information provided by auth
        // So far it is hardcoded the same user id
        long loggedUserId = id;
        boolean isLoggedUserAdmin = false;
        for (GrantedAuthority grantedAuthority : auth.getAuthorities()){
            if (grantedAuthority.getAuthority().equals(RoleName.ADMIN.name())){
                isLoggedUserAdmin = true;
            }
        }

        Optional<User> user = userRepository.findById(id);

        if (loggedUserId != id && !isLoggedUserAdmin)
            throw new CustomException("You are not allow to delete other users different than you.");

        if (user.isEmpty())
            throw new CustomException("No user with id: " + id);

        if (user.get().getSoftDelete())
            throw new CustomException("The user is already deleted.");

        user.get().setSoftDelete(true);
        return "User " + id + " successfully deleted.";
    }

	@Override
	public User save(User user) {
		User existUser = userRepository.findByEmail(user.getEmail());
		
		if(existUser!=null) {
			throw new CustomException("Email already exist");
		}
		
		return userRepository.save(user);		
	}

}
