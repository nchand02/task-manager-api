package com.encora.taskmanager.controller;

import com.encora.taskmanager.dto.UserDto;
import com.encora.taskmanager.entity.User;
import com.encora.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final PagedResourcesAssembler<User> assembler;

    private final UserService userService;

    public UserController(UserService userService, PagedResourcesAssembler<User> assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<User>>> getAllUserPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> usersPaged = userService.getAllUsersPaginated(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(usersPaged));
    }

}
