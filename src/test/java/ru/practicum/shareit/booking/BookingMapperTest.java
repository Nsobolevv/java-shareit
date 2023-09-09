package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingMapperTest {
    @Autowired
    BookingMapper bookingMapper;
    private final UserDto userDto = new UserDto(1, "email@email.com", "name");
    private final User user = new User(1, "email@email.com", "name");
    private final Booking booking = new Booking(1L, null, user, null,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final BookingDto bookingDto = new BookingDto(1L, null, userDto, null,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final BookingInputDto bookingInputDto = new BookingInputDto(1L, 1L,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final BookingDtoShort bookingDtoShort = new BookingDtoShort(1L, null, 1L,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));

    @Test
    void convertToDtoTest() {
        assertEquals(bookingDto, bookingMapper.convertToDto(booking));
    }

    @Test
    void convertToDtoShortTest() {
        assertEquals(bookingDtoShort, bookingMapper.convertToDtoShort(booking));
    }

    @Test
    void convertFromDtoTest() {
        Booking booking2 = bookingMapper.convertFromDto(bookingInputDto);
        assertEquals(booking.getId(), booking2.getId());
        assertEquals(booking.getStart(), booking2.getStart());
        assertEquals(booking.getEnd(), booking2.getEnd());

    }

}