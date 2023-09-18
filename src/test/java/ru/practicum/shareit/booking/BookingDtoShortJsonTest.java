package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.booking.dto.BookingDtoShort;


import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingDtoShortJsonTest {
    @Autowired
    private JacksonTester<BookingDtoShort> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDtoShort(1L, null, 1L,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }

}