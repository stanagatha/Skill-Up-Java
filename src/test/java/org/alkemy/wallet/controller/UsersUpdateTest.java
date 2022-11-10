package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.dto.UserUpdateDto;
import org.alkemy.wallet.mapper.UserMapper;
import org.alkemy.wallet.model.Role;
import org.alkemy.wallet.model.RoleName;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersUpdateTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IUserRepository userRepositoryMock;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserMapper userMapper;

    private String userToken;
    private User admin, user;
    private UserUpdateDto userUpdateDto;



    @BeforeEach
    public void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "ADMIN Role", new Date(), new Date());
        user = new User("UserFN", "UserLN", "userEmail@email.com", "1234", userRole);
        admin = new User("AdminFN", "AdminLN", "adminEmail@email.com", "5678", adminRole);
        user.setId(1L);
        admin.setId(2L);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepositoryMock.findByEmail(admin.getEmail())).thenReturn(admin);
        when(userRepositoryMock.findById(admin.getId())).thenReturn(Optional.of(admin));
        UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority(RoleName.USER.name())));
        userToken = jwtTokenUtil.generateToken(loggedUserDetails);

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("New name");
        userUpdateDto.setLastName("New last name");

        jsonMapper = new ObjectMapper();
    }

    @Test
    void patch_TokenProvided_OkResponse() throws Exception {
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        UserDto responseUserDto = userMapper.userToUserDTO(user);
        when(userRepositoryMock.save(user)).thenReturn(user);
        mockMvc.perform(patch("/users/" + user.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonMapper.writeValueAsString(responseUserDto)));
    }

    @Test
    void patch_NoTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(patch("/users/" + user.getId()).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }
}
