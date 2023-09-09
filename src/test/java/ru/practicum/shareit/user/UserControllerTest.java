package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final UserDto dto = new UserDto(1, "email@email.com", "name");

    @Test
    void addUserTest() throws Exception {
        when(service.addUser(any()))
                .thenReturn(dto);
        String response = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).addUser(dto);
        assertEquals(mapper.writeValueAsString(dto), response);
    }

    @Test
    void addUserInvalidEmailTest() throws Exception {
        UserDto invalidEmailDto = new UserDto(1, "email.email.com", "name");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidEmailDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(invalidEmailDto);
    }

    @Test
    void addUserInvalidNameTest() throws Exception {
        UserDto invalidNameDto = new UserDto(1, "email@email.com", "");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidNameDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(invalidNameDto);
    }

    @Test
    void getAllTest() throws Exception {
        when(service.getAllUsers())
                .thenReturn(List.of(dto));
        String response = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(dto)), response);
    }

    @Test
    void getByIdTest() throws Exception {
        when(service.getUserById(anyLong()))
                .thenReturn(dto);
        String response = mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(service.getUserById(1)), response);
    }

    @Test
    void getByIdNotExistUserTest() throws Exception {
        when(service.getUserById(anyLong()))
                .thenThrow(new ObjectNotFoundException(String.format("Пользователь с id %s не найден", 99)));
        mvc.perform(get("/users/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTest() throws Exception {
        UserDto updatedDto = new UserDto(1, "upEmail@email.com", "upName");
        when(service.updateUser(anyLong(), any()))
                .thenReturn(updatedDto);
        String response = mvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).updateUser(1, updatedDto);
        assertEquals(mapper.writeValueAsString(updatedDto), response);
    }

    @Test
    void updateInvalidEmailTest() throws Exception {
        UserDto invalidUpdatedDto = new UserDto(1, "email.email.com", "name");
        mvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidUpdatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).updateUser(1, invalidUpdatedDto);
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
        verify(service).removeUser(1);
    }
}