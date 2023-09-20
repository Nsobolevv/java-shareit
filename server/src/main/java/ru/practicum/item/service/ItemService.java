package ru.practicum.item.service;

import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItems(Integer from, Integer size, Long userId);

    List<ItemDto> searchItems(Integer from, Integer size, String text);

    void removeItem(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}