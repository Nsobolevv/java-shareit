package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import ru.practicum.item.dto.Item;
import ru.practicum.user.dto.User;


import java.time.LocalDateTime;

import java.util.Set;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private Set<Item> items;

}
