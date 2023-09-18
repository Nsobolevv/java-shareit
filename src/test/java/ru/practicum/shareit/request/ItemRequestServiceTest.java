package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository users;

    @Mock
    private ItemRequestMapper mapper;

    private final User user = new User(1, "name", "email@email.com");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.of(2023, 9, 1, 0, 0), new HashSet<>());
    private final ItemRequestDto itemRequestDto = new ItemRequestDto("description");
    private final ItemRequest newItemRequest = new ItemRequest(0L, "description", null, null, null);
    private final ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description", LocalDateTime.of(2023, 9, 1, 0, 0));
    private final RequestDtoResponseWithMD requestDtoResponseWithMD = new RequestDtoResponseWithMD(1L, "description", LocalDateTime.of(2023, 9, 1, 0, 0), new HashSet<>());
    private final ItemRequestListDto itemRequestListDto = new ItemRequestListDto(List.of(requestDtoResponseWithMD));

    @Test
    void addTest() {
        when(users.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(mapper.mapToItemRequest(itemRequestDto))
                .thenReturn(newItemRequest);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(mapper.mapToItemRequestDtoResponse(any()))
                .thenReturn(itemRequestDtoResponse);
        assertEquals(itemRequestService.createItemRequest(itemRequestDto, 1L), itemRequestDtoResponse);
    }


    @Test
    void addTestWithFailId() {
        when(users.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.createItemRequest(itemRequestDto, 1L));
        assertEquals(e.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void getPrivateRequestsTest() throws PaginationException {
        when(users.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(any(), anyLong()))
                .thenReturn(List.of(itemRequest));
        when(mapper.mapToRequestDtoResponseWithMD(List.of(itemRequest)))
                .thenReturn(List.of(requestDtoResponseWithMD));
        assertEquals(itemRequestService.getPrivateRequests(0, 10, 1L), itemRequestListDto);
    }

    @Test
    void getPrivateRequestsWrongIdTest() {
        when(users.existsById(anyLong()))
                .thenReturn(false);
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getPrivateRequests(1, 10, 3L));
        assertEquals(e.getMessage(), "Пользователь с id 3 не найден");
    }

    @Test
    void getOtherRequestsTest() throws PaginationException {
        when(users.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(any(), anyLong()))
                .thenReturn(List.of(itemRequest));
        when(mapper.mapToRequestDtoResponseWithMD(List.of(itemRequest)))
                .thenReturn(List.of(requestDtoResponseWithMD));
        assertEquals(itemRequestService.getOtherRequests(0, 10, 2L), itemRequestListDto);
    }

    @Test
    void getOtherRequestsWrongIdTest() {
        when(users.existsById(anyLong()))
                .thenReturn(false);
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getOtherRequests(0, 1, 3L));
        assertEquals(e.getMessage(), "Пользователь с id 3 не найден");
    }

    @Test
    void getItemRequestTest() {
        when(users.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when((mapper.mapToRequestDtoResponseWithMD(itemRequest)))
                .thenReturn(requestDtoResponseWithMD);
        assertEquals(itemRequestService.getItemRequest(1L, 1L), requestDtoResponseWithMD);
    }

    @Test
    void getItemRequestWrongIdTest() {
        when(users.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequest(1L, 3L));
        assertEquals(e.getMessage(), "Запроса с id=3 нет");
    }

    @Test
    void getItemRequestWrongUserIdTest() {
        when(users.existsById(anyLong()))
                .thenReturn(false);
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequest(1L, 3L));
        assertEquals(e.getMessage(), "Пользователь с id 1 не найден");
    }
}