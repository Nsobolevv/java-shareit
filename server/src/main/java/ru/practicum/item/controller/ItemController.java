package ru.practicum.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(userIdHeader) long userId, @RequestBody ItemDto itemDto) {
        log.info("User {} add item {}", userId, itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(userIdHeader) long userId) {
        log.info("Get item by item id {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(userIdHeader) Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all items by user id {}", userId);
        return itemService.getAllItems(from, size, userId);
    }


    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search items by text {}", text);
        return itemService.searchItems(from, size, text);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        log.info("User {} updated item by id {}", userId, itemId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("{itemId}")
    public void removeItem(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId) {
        log.info("User {} deleted item by id {}", userId, itemId);
        itemService.removeItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("User {} add comment to item {}", userId, itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }


}