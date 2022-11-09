package org.alkemy.wallet.controller;

import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(@RequestParam("page") Integer pageNumber) {
        return ResponseEntity.ok().body(userService.getAll(pageNumber));
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
