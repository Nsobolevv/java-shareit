package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;


@Builder
@Data
@Jacksonized
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
}
