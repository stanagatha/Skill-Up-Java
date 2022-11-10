package org.alkemy.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alkemy.wallet.dto.UserDto;
import org.alkemy.wallet.dto.UserUpdateDto;
import org.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Users", description = "UserController")
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users",
            description = "Only accessible as an ADMIN.")
    @Secured({"ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(@RequestParam("page") Integer pageNumber) {
        return ResponseEntity.ok().body(userService.getAll(pageNumber));
    }

    @GetMapping("/current")
    @Operation(summary = "Get information from the currently authenticated user")
    public ResponseEntity<UserDto> getCurrent() {
        return ResponseEntity.ok().body(userService.getCurrent());
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a user",
            description = "As an ADMIN, can delete any user. As a USER, can only delete themself.")
    public ResponseEntity<String> deleteById(@PathVariable("userId") Long id) {
        return ResponseEntity.ok().body(userService.deleteById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a user",
            description = "Can't modify \"mail\" nor \"role\" fields.")
    public ResponseEntity<UserDto> UpdateUser(@PathVariable("id") Long id,
                                              @RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.ok().body(userService.updateUser(id, userUpdateDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user information from provided ID")
    public ResponseEntity<UserDto> getById(@PathVariable Long id){
        return ResponseEntity.ok().body(userService.getById(id));
    }

}
