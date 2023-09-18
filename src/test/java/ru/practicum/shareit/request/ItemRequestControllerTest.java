package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto("description");
    private final ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description", LocalDateTime.of(2023, 9, 1, 0, 0));
    private final RequestDtoResponseWithMD requestDtoResponseWithMD = new RequestDtoResponseWithMD(1L, "description", LocalDateTime.of(2023, 9, 1, 0, 0), new HashSet<>());
    private final ItemRequestListDto itemRequestListDto = new ItemRequestListDto(List.of(requestDtoResponseWithMD));

    @Test
    void addTest() throws Exception {
        when(service.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDtoResponse);
        String response = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDtoResponse))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).createItemRequest(itemRequestDto, 1L);
        assertEquals(mapper.writeValueAsString(itemRequestDtoResponse), response);
    }

    @Test
    void addInvalidRequestTest() throws Exception {
        ItemRequestDto invalidDto = new ItemRequestDto("");
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPrivateRequestsTest() throws Exception, PaginationException {
        when(service.getPrivateRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(itemRequestListDto);
        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getPrivateRequests(0, 10, 1L);
        assertEquals(mapper.writeValueAsString(itemRequestListDto), response);
    }

    @Test
    void getPrivateRequestsNotExistItemTest() throws Exception, PaginationException {
        ItemRequestListDto itemRequestListDto = new ItemRequestListDto(new ArrayList<>());
        when(service.getPrivateRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(itemRequestListDto);
        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getPrivateRequests(0, 10, 1L);
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
    }

    @Test
    void getOtherRequestsTest() throws PaginationException, Exception {
        when(service.getOtherRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(itemRequestListDto);
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getOtherRequests(0, 10, 1L);
        assertEquals(mapper.writeValueAsString(itemRequestListDto), response);
    }

    @Test
    void getOtherRequestsNotExistItemTest() throws Exception, PaginationException {
        ItemRequestListDto itemRequestListDto = new ItemRequestListDto(new ArrayList<>());
        when(service.getOtherRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(itemRequestListDto);
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getOtherRequests(0, 10, 1L);
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(service.getItemRequest(anyLong(), anyLong()))
                .thenReturn(requestDtoResponseWithMD);
        String response = mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getItemRequest(1L, 1L);
        assertEquals(mapper.writeValueAsString(requestDtoResponseWithMD), response);
    }

    @Test
    void getByIdNotExistTest() throws Exception {
        when(service.getItemRequest(anyLong(), anyLong()))
                .thenThrow(new ObjectNotFoundException(String.format("Запроса с id=%s нет", 1)));
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}