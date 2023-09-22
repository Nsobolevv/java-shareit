package ru.practicum.user;


import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    void removeUser(long userId);
}