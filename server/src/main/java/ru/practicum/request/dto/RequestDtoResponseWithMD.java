package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
public class RequestDtoResponseWithMD {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Set<ItemDataForRequestDto> items;

}
