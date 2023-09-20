package ru.practicum.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.Marker;
import ru.practicum.user.dto.UserDto;
import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("User {} added", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Get user by id {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Get all users");
        return userService.getAllUsers();
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("User by id {} updated", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void removeUser(@PathVariable long userId) {
        userService.removeUser(userId);
        log.info("User by id {} deleted", userId);
    }
}