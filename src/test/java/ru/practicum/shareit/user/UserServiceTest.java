package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;
    @Mock
    UserMapper userMapper;
    @Mock
    private UserRepository repository;
    private final User user = new User(1, "email@email.com", "name");
    private final UserDto userDto = new UserDto(1, "email@email.com", "name");

    @Test
    void addTest() {
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(userMapper.convertToDto(any()))
                .thenReturn(userDto);
        when(repository.save(any()))
                .thenReturn(user);
        assertEquals(service.addUser(userDto), userDto);
    }

    @Test
    void getAllTest() {
        when(repository.findAll())
                .thenReturn(List.of(user));
        when(userMapper.convertToDto(any()))
                .thenReturn(userDto);
        assertEquals(service.getAllUsers(), List.of(userDto));
    }

    @Test
    void getAllWhenNoUsersTest() {
        when(repository.findAll())
                .thenReturn(new ArrayList<>());
        assertEquals(service.getAllUsers(), new ArrayList<>());
    }

    @Test
    void getByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userMapper.convertToDto(any()))
                .thenReturn(userDto);
        assertEquals(service.getUserById(1), userDto);
    }

    @Test
    void getByIdIfNotExistTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> service.getUserById(1));
        assertEquals(e.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void updateTest() {
        User updatedUser = new User(1, "upEmail@email.com", "upName");
        UserDto updatedDto = new UserDto(1, "upEmail@email.com", "upName");
        when(repository.findById(any()))
                .thenReturn(Optional.of(user));
        when(repository.save(any()))
                .thenReturn(updatedUser);
        when(userMapper.convertToDto(any()))
                .thenReturn(updatedDto);
        assertEquals(service.updateUser(1, userDto), updatedDto);
    }

    @Test
    void updateWhenUserIsNotExist() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> service.updateUser(1, userDto));
        assertEquals(e.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void deleteTest() {
        repository.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}