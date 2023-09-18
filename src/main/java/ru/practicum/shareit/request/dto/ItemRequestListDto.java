package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ItemRequestListDto {
    @JsonValue
    private List<RequestDtoResponseWithMD> requests;
}
