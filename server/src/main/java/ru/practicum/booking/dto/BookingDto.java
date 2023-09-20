package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.model.Status;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;
    private ItemDto item;
    private UserDto booker;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}
