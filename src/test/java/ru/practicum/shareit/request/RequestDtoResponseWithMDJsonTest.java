package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoResponseWithMDJsonTest {
    @Autowired
    private JacksonTester<RequestDtoResponseWithMD> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new RequestDtoResponseWithMD(1L, "description", null, new HashSet<>());

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
    }
}
