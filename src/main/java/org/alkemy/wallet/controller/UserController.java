package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll(){
        return ResponseEntity.ok().body(userService.getAll());
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrent(){
        return ResponseEntity.ok().body(userService.getCurrent());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteById(@PathVariable("userId") Long id){
        return ResponseEntity.ok().body(userService.deleteById(id));
    }

}
