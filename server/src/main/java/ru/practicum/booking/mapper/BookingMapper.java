package ru.practicum.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoShort;
import ru.practicum.booking.dto.BookingInputDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingDto convertToDto(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoShort convertToDtoShort(Booking booking);

    @Mapping(target = "item.id", source = "itemId")
    Booking convertFromDto(BookingInputDto bookingInputDto);
}
