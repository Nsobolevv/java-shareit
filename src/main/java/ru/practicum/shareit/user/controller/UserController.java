package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final String host = "localhost";
    private final String port = "8080";
    private final String protocol = "http";
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) throws DataExistException {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users")
                .build();
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), userDto.toString());
        return userService.addUser(userDto);
    }

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users/{userId}")
                .build();
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), String.valueOf(userId));
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() throws DataExistException {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users")
                .build();
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "пусто");
        return userService.getAllUsers();
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) throws DataExistException {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users/{userId}")
                .build();
        Logger.logRequest(HttpMethod.PATCH, uriComponents.toUriString(), userDto.toString());
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void removeUser(@PathVariable long userId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users/{userId}")
                .build();
        Logger.logRequest(HttpMethod.DELETE, uriComponents.toUriString(), String.valueOf(userId));
        userService.removeUser(userId);
    }
}