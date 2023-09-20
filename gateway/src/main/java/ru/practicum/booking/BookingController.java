package ru.practicum.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingInputDto;
import ru.practicum.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@AllArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping    // Добавление нового запроса на бронирование.
    public ResponseEntity<Object> addBooking(@RequestHeader(userIdHeader) long userId,
                                             @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingClient.create(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")   // Подтверждение или отклонение запроса на бронирование.
    public ResponseEntity<Object> approveOrRejectBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                                         @RequestHeader(userIdHeader) long userId) {
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")   // Получение данных о конкретном бронировании (включая его статус)
    public ResponseEntity<Object> getBookingById(@PathVariable long bookingId, @RequestHeader(userIdHeader) long userId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping   // Получение списка всех бронирований текущего пользователя (можно делать выборку по статусу).
    public ResponseEntity<Object> getBookingsOfCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                                           @RequestHeader(userIdHeader) long userId,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        State.convert(state);
        return bookingClient.getBookingsOfCurrentUser(userId, state, from, size);
    }

    // Получение списка бронирований для всех вещей текущего пользователя-владельца (можно делать выборку по статусу)
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader(userIdHeader) long userId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        State.convert(state);
        return bookingClient.getBookingsOfOwner(userId, state, from, size);
    }
}
