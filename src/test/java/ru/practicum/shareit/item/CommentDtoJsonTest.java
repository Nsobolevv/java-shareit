package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new CommentDto(1, "text", "author", LocalDateTime.now());

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPathValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
    }
}
