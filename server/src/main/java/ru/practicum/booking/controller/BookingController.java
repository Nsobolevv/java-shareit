package ru.practicum.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingInputDto;
import ru.practicum.booking.model.AccessLevel;
import ru.practicum.booking.model.State;
import ru.practicum.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping    // Добавление нового запроса на бронирование.
    public BookingDto addBooking(@RequestHeader(userIdHeader) long userId,
                                 @RequestBody BookingInputDto bookingInputDto) {
        log.info("User {} added booking", userId);
        return bookingService.addBooking(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")   // Подтверждение или отклонение запроса на бронирование.
    public BookingDto approveOrRejectBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                             @RequestHeader(userIdHeader) long userId) {
        log.info("User {} set status {} for booking {}", userId, approved, bookingId);
        return bookingService.approveOrRejectBooking(userId, bookingId, approved, AccessLevel.OWNER);
    }

    @GetMapping("/{bookingId}")   // Получение данных о конкретном бронировании (включая его статус)
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader(userIdHeader) long userId) {
        log.info("User {} get booking {}", userId, bookingId);
        return bookingService.getBooking(bookingId, userId, AccessLevel.OWNER_AND_BOOKER);
    }

    @GetMapping   // Получение списка всех бронирований текущего пользователя (можно делать выборку по статусу).
    public List<BookingDto> getBookingsOfCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader(userIdHeader) long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("User {} get all {} bookings", userId, state);
        return bookingService.getBookingsOfCurrentUser(State.valueOf(state), userId, from, size);
    }

    // Получение списка бронирований для всех вещей текущего пользователя-владельца (можно делать выборку по статусу)
    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(userIdHeader) long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("User {} get {} bookings on his items", userId, state);
        return bookingService.getBookingsOfOwner(State.valueOf(state), userId, from, size);
    }
}
