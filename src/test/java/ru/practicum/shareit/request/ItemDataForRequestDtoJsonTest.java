package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDataForRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDataForRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new ItemDataForRequestDto(1L, "name", "description", true, 1L);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
    }
}
