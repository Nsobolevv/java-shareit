package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItems(Integer from, Integer size, Long userId) throws PaginationException;

    List<ItemDto> searchItems(Integer from, Integer size, String text) throws PaginationException;

    void removeItem(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}