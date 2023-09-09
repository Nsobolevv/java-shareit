package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentMapperTest {
    @Autowired
    CommentMapper commentMapper;

    private final Comment comment = new Comment(1, "text", null, null, null);
    private final CommentDto commentDto = new CommentDto(1, "text", null, null);

    @Test
    void convertToDtoTest() {
        assertEquals(commentDto, commentMapper.convertToDto(comment));
    }

    @Test
    void convertFromDtoTest() {
        assertEquals(comment, commentMapper.convertFromDto(commentDto));
    }
}