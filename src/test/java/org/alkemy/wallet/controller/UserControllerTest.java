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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private String user1Token, user2Token, admin1Token;
    private User user1, user2, admin1;
    private List<User> userList;

    @BeforeEach
    private void setUp(){
        Role userRole = new Role(1L, RoleName.USER, "USER Role", new Date(), new Date());
        Role adminRole = new Role(2L, RoleName.ADMIN, "ADMIN Role", new Date(), new Date());

        user1 = new User("User1FN", "User1LN", "user1Email@email.com", "1234", userRole);
        user2 = new User("User2FN", "User2LN", "user2Email@email.com", "4321", userRole);
        admin1 = new User("Admin1FN", "Admin1LN", "admin1Email@email.com", "5678", adminRole);
        user1.setId(1L);
        user2.setId(2L);
        admin1.setId(3L);

        userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(admin1);

        UserDetails loggedUser1Details = new org.springframework.security.core.userdetails.User(user1.getEmail(), user1.getPassword(), new ArrayList<>());
        UserDetails loggedUser2Details = new org.springframework.security.core.userdetails.User(user2.getEmail(), user2.getPassword(), new ArrayList<>());
        UserDetails loggedAdmin1Details = new org.springframework.security.core.userdetails.User(admin1.getEmail(), admin1.getPassword(), new ArrayList<>());
        user1Token = jwtTokenUtil.generateToken(loggedUser1Details);
        user2Token = jwtTokenUtil.generateToken(loggedUser2Details);
        admin1Token = jwtTokenUtil.generateToken(loggedAdmin1Details);

        when(userRepositoryMock.findByEmail(user1.getEmail())).thenReturn(user1);
        when(userRepositoryMock.findByEmail(user2.getEmail())).thenReturn(user2);
        when(userRepositoryMock.findByEmail(admin1.getEmail())).thenReturn(admin1);
        when(userRepositoryMock.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(userRepositoryMock.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
        when(userRepositoryMock.findById(admin1.getId())).thenReturn(Optional.ofNullable(admin1));
        when(userRepositoryMock.findAll()).thenReturn(userList);
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
                        header("Authorization", "Bearer " + user1Token + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_UserRole_ForbiddenResponse() throws Exception {
        mockMvc.perform(get("/users").
                        header("Authorization", "Bearer " + user1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());
    }

    @Test
    void getAll_AdminRole_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userList.stream().map(userMapper::userToUserDTO).collect(Collectors.toList()));
        mockMvc.perform(get("/users").
                        header("Authorization", "Bearer " + admin1Token).
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
    void getCurrent_UserRole_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userMapper.userToUserDTO(user1));
        mockMvc.perform(get("/users/current").
                        header("Authorization", "Bearer " + user1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }

    @Test
    void getCurrent_AdminRole_OkResponse() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(userMapper.userToUserDTO(admin1));
        mockMvc.perform(get("/users/current").
                        header("Authorization", "Bearer " + admin1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().json(expectedJson));
    }

    @Test
    void deleteById_NoTokenProvided_UnauthorizedResponse() throws Exception {
        assertEquals(false, user1.getSoftDelete());

        mockMvc.perform(delete("/users/" + user1.getId()).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());

        assertEquals(false, user1.getSoftDelete());
    }

    @Test
    void deleteById_WrongTokenProvided_UnauthorizedResponse() throws Exception {
        assertEquals(false, user1.getSoftDelete());

        mockMvc.perform(delete("/users/" + user1.getId()).
                        header("Authorization", "Bearer " + user1Token + "b").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());

        assertEquals(false, user1.getSoftDelete());
    }

    @Test
    void deleteById_UserRoleTryToDeleteOtherUser_ForbiddenResponse() throws Exception {
        assertEquals(false, user2.getSoftDelete());

        mockMvc.perform(delete("/users/" + user2.getId()).
                        header("Authorization", "Bearer " + user1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());

        assertEquals(false, user2.getSoftDelete());
    }

    @Test
    void deleteById_UserRoleTryToDeleteAdmin_ForbiddenResponse() throws Exception {
        assertEquals(false, admin1.getSoftDelete());

        mockMvc.perform(delete("/users/" + admin1.getId()).
                        header("Authorization", "Bearer " + user1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());

        assertEquals(false, admin1.getSoftDelete());
    }

    @Test
    void deleteById_UserRoleTryToSelfDelete_OkResponse() throws Exception {
        assertEquals(false, user1.getSoftDelete());

        mockMvc.perform(delete("/users/" + user1.getId()).
                        header("Authorization", "Bearer " + user1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        assertEquals(true, user1.getSoftDelete());
    }

    @Test
    void deleteById_AdminRoleTryToDeleteOther_OkResponse() throws Exception {
        assertEquals(false, user1.getSoftDelete());

        mockMvc.perform(delete("/users/" + user1.getId()).
                        header("Authorization", "Bearer " + admin1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        assertEquals(true, user1.getSoftDelete());
    }

    @Test
    void deleteById_AdminRoleTryToSelfDelete_OkResponse() throws Exception {
        assertEquals(false, admin1.getSoftDelete());

        mockMvc.perform(delete("/users/" + admin1.getId()).
                        header("Authorization", "Bearer " + admin1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        assertEquals(true, admin1.getSoftDelete());
    }

    @Test
    void deleteById_AdminRoleTryToDeleteNotExistent_NotFoundResponse() throws Exception {
        mockMvc.perform(delete("/users/8").
                        header("Authorization", "Bearer " + admin1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound());
    }

    @Test
    void deleteById_AdminRoleTryToDeleteUserAlreadyDeleted_BadRequestResponse() throws Exception {
        assertEquals(false, user1.getSoftDelete());

        mockMvc.perform(delete("/users/" + user1.getId()).
                        header("Authorization", "Bearer " + admin1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        assertEquals(true, user1.getSoftDelete());

        mockMvc.perform(delete("/users/" + user1.getId()).
                        header("Authorization", "Bearer " + admin1Token).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

        assertEquals(true, user1.getSoftDelete());
    }
}