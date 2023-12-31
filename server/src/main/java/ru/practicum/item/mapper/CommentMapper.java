package ru.practicum.item.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.model.Comment;


@Component
public class CommentMapper {
    private final ModelMapper modelMapper;

    public CommentMapper() {
        modelMapper = new ModelMapper();
    }

    public CommentDto convertToDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    public Comment convertFromDto(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }
}
