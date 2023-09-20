package ru.practicum.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "request.id", target = "requestId")
    ItemDto convertToDto(Item item);

    Item convertFromDto(ItemDto itemDto);
}