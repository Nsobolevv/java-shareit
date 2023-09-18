package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.exception.ObjectNotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService service;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final User user = new User(1, "email@email.com", "name");
    private final ItemRequest itemRequest1 = new ItemRequest(1L, "request", user, LocalDateTime.now(), new HashSet<>());
    private final Item item = new Item(1L, user.getId(), "name", "description", true, itemRequest1);
    private final BookingDtoShort nextBookingDtoShort = new BookingDtoShort(1L, item, 1L, LocalDateTime.of(2023, 10, 10, 0, 0),
            LocalDateTime.of(2023, 10, 20, 0, 0));
    private final BookingDtoShort lastBookingDtoShort = new BookingDtoShort(2L, item, 1L, LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0));
    private final CommentDto commentDto = new CommentDto(1, "text", "name1", LocalDateTime.of(2023, 8, 31, 0, 0));
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L,
            lastBookingDtoShort, nextBookingDtoShort, List.of(commentDto));

    @Test
    void addTest() throws Exception {
        when(service.addItem(anyLong(), any()))
                .thenReturn(itemDto);
        String response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).addItem(1, itemDto);
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    void addWrongNameTest() throws Exception {
        ItemDto wrongNameDto = new ItemDto(1L, "", "description", true, 1L,
                lastBookingDtoShort, nextBookingDtoShort, List.of(commentDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongNameDto)))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(1, wrongNameDto);
    }

    @Test
    void addWrongDescriptionTest() throws Exception {
        ItemDto wrongDescriptionDto = new ItemDto(1L, "name", "", true, 1L,
                lastBookingDtoShort, nextBookingDtoShort, List.of(commentDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongDescriptionDto)))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(1, wrongDescriptionDto);
    }

    @Test
    void addWrongAvailableTest() throws Exception {
        ItemDto wrongAvailableDto = new ItemDto(1L, "name", "", null, 1L,
                lastBookingDtoShort, nextBookingDtoShort, List.of(commentDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongAvailableDto)))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(1, wrongAvailableDto);
    }

    @Test
    void getTest() throws Exception {
        when(service.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        String response = mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    void getNotExistItemTest() throws Exception {
        when(service.getItemById(anyLong(), anyLong()))
                .thenThrow(new ObjectNotFoundException(String.format("Вещь с id %s не найдена", 99)));
        mvc.perform(get("/items/{itemId}", 99)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception, PaginationException {
        when(service.getAllItems(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemDto));
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(itemDto)), response);
    }

    @Test
    void searchTest() throws Exception, PaginationException {
        when(service.searchItems(anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(itemDto));
        String response = mvc.perform(get("/items/search?text={text}", "item")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(itemDto)), response);
    }

    @Test
    void searchEmptyTextTest() throws Exception, PaginationException {
        when(service.searchItems(anyInt(), anyInt(), anyString()))
                .thenReturn(new ArrayList<>());
        String response = mvc.perform(get("/items/search?text={text}", "")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
    }

    @Test
    void updateTest() throws Exception {
        ItemDto updatedDto = new ItemDto(1, "updatedName", "updatedDescription", true,
                null, null, null, new ArrayList<>());
        when(service.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(updatedDto);
        String response = mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(updatedDto), response);
    }

    @Test
    void updateNotExistItemTest() throws Exception {
        ItemDto updatedDto = new ItemDto(99, "updatedName", "updatedDescription", true,
                null, null, null, new ArrayList<>());
        when(service.updateItem(anyLong(), anyLong(), any()))
                .thenThrow(new ObjectNotFoundException(String.format("Вещь с id %s не найдена", 99)));
        mvc.perform(patch("/items/{itemId}", 99)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNotExistItemForUserTest() throws Exception {
        ItemDto updatedDto = new ItemDto(1, "updatedName", "updatedDescription", true,
                null, null, null, new ArrayList<>());
        when(service.updateItem(anyLong(), anyLong(), any()))
                .thenThrow(new ObjectNotFoundException(String.format("У пользователя с id %s не найдена вещь с id %s",
                        1, 1)));
        mvc.perform(patch("/items/{itemId}", 99)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeItemTest() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(service).removeItem(1, 1);
    }

    @Test
    void addCommentTest() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        String response = mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(commentDto), response);
    }

    @Test
    void addCommentNotExistItemTest() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new ObjectNotFoundException(String.format("Вещь с id %s не найдена", 1)));
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCommentNotExistItemForUserTest() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new ObjectNotFoundException(String.format("Пользователь с id %d не арендовал вещь с id %d.", 1, 1)));
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCommentNotPossibilityForUserTest() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new ObjectNotAvailableException(String.format("Пользователь с id %d не может оставлять комментарии вещи " +
                        "с id %d.", 1, 1)));
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}