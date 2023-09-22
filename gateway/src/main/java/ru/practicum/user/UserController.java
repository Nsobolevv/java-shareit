package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.Marker;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addUser(@RequestBody @Validated(UserDto.NewUser.class) UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable long userId) {
        return userClient.deleteUser(userId);
    }
}