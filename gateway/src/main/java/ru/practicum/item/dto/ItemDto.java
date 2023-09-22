package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.booking.dto.BookingDtoShort;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;

    @NotBlank(message = "Поле с именем не должно быть пустым.")
    private String name;

    @NotBlank(message = "Поле с описанием не должно быть пустым.")
    private String description;

    @NotNull(message = "Поле Available не должно быть пустым.")
    private Boolean available;
    private Long requestId;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
}