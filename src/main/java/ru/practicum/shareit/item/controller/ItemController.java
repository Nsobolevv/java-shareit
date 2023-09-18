package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;


import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.logger.Logger;

import javax.validation.Valid;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String host = "localhost";
    private final String port = "8080";
    private final String protocol = "http";
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(userIdHeader) long userId, @Valid @RequestBody ItemDto itemDto) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/items")
                .build();
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), itemDto.toString());
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(userIdHeader) long userId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/items/{itemId}")
                .build();
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), String.valueOf(userId));
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(userIdHeader) @Positive Long userId,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) throws PaginationException {

        return itemService.getAllItems(from, size, userId);
    }


    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) throws PaginationException {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/items/")
                .query("search?text={text}")
                .build();
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), text);
        return itemService.searchItems(from, size, text);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/items/{itemId}")
                .build();
        Logger.logRequest(HttpMethod.PATCH, uriComponents.toUriString(), itemDto.toString());
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("{itemId}")
    public void removeItem(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId) {
        itemService.removeItem(userId, itemId);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/items/{itemId}")
                .build();
        Logger.logRequest(HttpMethod.DELETE, uriComponents.toUriString(), String.valueOf(itemId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/items/{itemId}/comment")
                .build();
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), commentDto.toString());
        return itemService.addComment(userId, itemId, commentDto);
    }


}