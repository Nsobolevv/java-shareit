package ru.practicum.booking.service;




import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingInputDto;
import ru.practicum.booking.model.AccessLevel;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(long bookerId, BookingInputDto bookingInputDto);

    BookingDto approveOrRejectBooking(long ownerId, long bookingId, boolean approved, AccessLevel accessLevel);

    Booking getBookingById(long bookingId, long userId, AccessLevel accessLevel);

    BookingDto getBooking(long bookingId, long userId, AccessLevel accessLevel);

    List<BookingDto> getBookingsOfCurrentUser(State state, long bookerId, Integer from, Integer size);

    List<BookingDto> getBookingsOfOwner(State state, long ownerId, Integer from, Integer size);
}
