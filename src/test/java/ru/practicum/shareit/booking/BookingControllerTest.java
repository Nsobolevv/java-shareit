package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService service;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private final UserDto userDto = new UserDto(1, "name", "email@email.com");
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, null,
            null, null, new ArrayList<>());
    private final BookingInputDto bookingInputDto = new BookingInputDto(1L, 1L,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final BookingDto bookingDto = new BookingDto(1L, itemDto, userDto, Status.WAITING,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));

    private final BookingDto bookingDtoAPPROVED = new BookingDto(2L, itemDto, userDto, Status.APPROVED,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));

    private final BookingDto bookingDtoREJECTED = new BookingDto(2L, itemDto, userDto, Status.REJECTED,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));

    @Test
    void addTest() throws Exception {
        when(service.addBooking(anyLong(), any()))
                .thenReturn(bookingDto);
        String response = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).addBooking(2, bookingInputDto);
        assertEquals(mapper.writeValueAsString(bookingDto), response);
    }

    @Test
    void addTestWrongStartData() throws Exception {
        BookingInputDto bookingInputDtoWrongData = new BookingInputDto(1L, 1L,
                LocalDateTime.of(2022, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingInputDtoWrongData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(2, bookingInputDtoWrongData);
    }

    @Test
    void addTestWrongEndData() throws Exception {
        BookingInputDto bookingInputDtoWrongData = new BookingInputDto(1L, 1L,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2022, 10, 30, 0, 0));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingInputDtoWrongData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(2, bookingInputDtoWrongData);
    }

    @Test
    void addTestNotStartData() throws Exception {
        BookingInputDto bookingInputDtoWrongData = new BookingInputDto(1L, 1L,
                null, LocalDateTime.of(2023, 10, 30, 0, 0));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingInputDtoWrongData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(2, bookingInputDtoWrongData);
    }

    @Test
    void addTestNotEndData() throws Exception {
        BookingInputDto bookingInputDtoWrongData = new BookingInputDto(1L, 1L,
                LocalDateTime.of(2023, 10, 30, 0, 0), null);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingInputDtoWrongData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(2, bookingInputDtoWrongData);
    }

    @Test
    void approveTrueTest() throws Exception {
        when(service.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean(), any()))
                .thenReturn(bookingDtoAPPROVED);
        String response = mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoAPPROVED))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).approveOrRejectBooking(1, 1, true, AccessLevel.OWNER);
        assertEquals(mapper.writeValueAsString(bookingDtoAPPROVED), response);
    }

    @Test
    void approveFalseTest() throws Exception {
        when(service.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean(), any()))
                .thenReturn(bookingDtoREJECTED);
        String response = mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, false)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoREJECTED))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).approveOrRejectBooking(1, 1, false, AccessLevel.OWNER);
        assertEquals(mapper.writeValueAsString(bookingDtoREJECTED), response);
    }

    @Test
    void approveWrongApprovedTest() throws Exception {
        when(service.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean(), any()))
                .thenThrow(new InvalidDataException(String.format("У бронирования с id 1 уже стоит статус APPROVED")));
        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoAPPROVED))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(service.getBooking(anyLong(), anyLong(), any()))
                .thenReturn(bookingDto);
        String response = mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getBooking(1, 1, AccessLevel.OWNER_AND_BOOKER);
        assertEquals(mapper.writeValueAsString(bookingDto), response);
    }

    @Test
    void getBookingByIdWrongBookingIdTest() throws Exception {
        when(service.getBooking(anyLong(), anyLong(), any()))
                .thenThrow(new ObjectNotFoundException(String.format("Бронирование с id 1 не найдено")));
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingByIdWrongUserIdTest() throws Exception {
        when(service.getBooking(anyLong(), anyLong(), any()))
                .thenThrow(new AccessException(String.format("У пользователя с id 1 нет прав на просмотр бронирования с id 1")));
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsOfCurrentUserTest() throws Exception, PaginationException {
        when(service.getBookingsOfCurrentUser(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getBookingsOfCurrentUser(any(), anyLong(), anyInt(), anyInt());
        assertEquals(mapper.writeValueAsString(List.of(bookingDto)), response);
    }

    @Test
    void getBookingsOfCurrentUserWrongStateTest() throws Exception {
        mvc.perform(get("/bookings?state={state}", "abc")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getBookingsOfOwnerTest() throws Exception, PaginationException {
        when(service.getBookingsOfOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getBookingsOfOwner(any(), anyLong(), anyInt(), anyInt());
        assertEquals(mapper.writeValueAsString(List.of(bookingDto)), response);
    }

    @Test
    void getBookingsOfOwnerWrongStateTest() throws Exception {

        mvc.perform(get("/bookings/owner?state={state}", "abc")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}