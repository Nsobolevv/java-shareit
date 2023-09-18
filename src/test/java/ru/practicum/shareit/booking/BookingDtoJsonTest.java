package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDto(1L, null, null, Status.WAITING,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }

}