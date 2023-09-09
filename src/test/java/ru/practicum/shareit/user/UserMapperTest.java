package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {
    @Autowired
    UserMapper userMapper;
    private final User user = new User(1, "email@email.com", "name");
    private final UserDto userDto = new UserDto(1, "email@email.com", "name");

    @Test
    void convertToDtoTest() {
        assertEquals(userDto, userMapper.convertToDto(user));
    }

    @Test
    void convertFromDtoTest() {
        assertEquals(user, userMapper.convertFromDto(userDto));
    }


}