package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.logger.Logger;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final String host = "localhost";
    private final String port = "8080";
    private final String protocol = "http";

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.convertFromDto(userDto);
        User userSaved = userRepository.save(user);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users")
                .build();
        Logger.logSave(HttpMethod.POST, uriComponents.toUriString(), userSaved.toString());
        return userMapper.convertToDto(userSaved);
    }

    @Transactional
    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User targetUser = userRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", id)));
        if (userDto.getEmail() != null) {
            targetUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            targetUser.setName(userDto.getName());
        }
        User userSaved = userRepository.save(targetUser);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users/{id}")
                .build();
        Logger.logSave(HttpMethod.PATCH, uriComponents.toUriString(), userSaved.toString());
        return userMapper.convertToDto(userSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users/{userId}")
                .build();
        Logger.logSave(HttpMethod.GET, uriComponents.toUriString(), user.toString());
        return userMapper.convertToDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/users")
                .build();
        Logger.logSave(HttpMethod.GET, uriComponents.toUriString(), users.toString());
        return users
                .stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void removeUser(long id) {
        userRepository.deleteById(id);
    }
}