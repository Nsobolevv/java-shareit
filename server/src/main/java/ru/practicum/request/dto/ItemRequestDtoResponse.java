package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
}
