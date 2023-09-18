package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import java.time.LocalDateTime;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoResponse> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new ItemRequestDtoResponse(1L, "description", LocalDateTime.now());

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
    }
}
