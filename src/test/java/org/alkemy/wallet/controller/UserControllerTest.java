package org.alkemy.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private UserMapper userMapper;
    @Autowired private ObjectMapper jsonMapper;
    @MockBean private IUserRepository userRepositoryMock;

    // Test fixture
    private String userToken, adminToken;
    private User user, admin;
    private List<User> userList;

    @BeforeEach
    private void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "ADMIN Role", new Date(), new Date());

        user = new User("UserFN", "UserLN", "userEmail@email.com", "1234", userRole);
        admin = new User("AdminFN", "AdminLN", "adminEmail@email.com", "5678", adminRole);
        user.setId(1L);
        admin.setId(2L);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepositoryMock.findByEmail(admin.getEmail())).thenReturn(admin);

        userList = new ArrayList<>();
        userList.add(user);
        userList.add(admin);
        when(userRepositoryMock.findAll()).thenReturn(userList);

        UserDetails loggedUserDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        UserDetails loggedAdminDetails = new org.springframework.security.core.userdetails.User(admin.getEmail(), admin.getPassword(), new ArrayList<>());
        userToken = jwtTokenUtil.generateToken(loggedUserDetails);
        adminToken = jwtTokenUtil.generateToken(loggedAdminDetails);
    }


    @Test
    void getAll_NoTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/users").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_WrongTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/users").
                        header("Authorization", "Bearer " + userToken + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_UserRoleTokenProvided_ForbiddenResponse() throws Exception {
        mockMvc.perform(get("/users").
                        header("Authorization", "Bearer " + userToken).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());
    }

    @Test
    void getAll_AdminRoleTokenProvided_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userList.stream().map(userMapper::userToUserDTO).collect(Collectors.toList()));
        mockMvc.perform(get("/users").
                        header("Authorization", "Bearer " + adminToken).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }

    @Test
    void getCurrent_NoTokenProvided_UnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/users/current").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrent_UserRoleTokenProvided_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userMapper.userToUserDTO(user));
        mockMvc.perform(get("/users/current").
                        header("Authorization", "Bearer " + userToken).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }

    @Test
    void getCurrent_AdminRoleTokenProvided_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userMapper.userToUserDTO(admin));
        mockMvc.perform(get("/users/current").
                        header("Authorization", "Bearer " + adminToken).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }

}