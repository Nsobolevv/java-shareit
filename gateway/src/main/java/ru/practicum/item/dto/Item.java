package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.ItemRequest;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Item {
    private long id;
    private long userId;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
