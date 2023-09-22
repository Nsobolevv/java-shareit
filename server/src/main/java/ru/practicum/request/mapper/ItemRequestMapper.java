package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.item.model.Item;
import ru.practicum.request.dto.ItemDataForRequestDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestDtoResponse;
import ru.practicum.request.dto.RequestDtoResponseWithMD;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDtoResponse mapToItemRequestDtoResponse(ItemRequest itemRequest);

    @Mapping(source = "request.id", target = "requestId")
    ItemDataForRequestDto mapToItemDataForRequestDto(Item item);

    RequestDtoResponseWithMD mapToRequestDtoResponseWithMD(ItemRequest itemRequest);

    List<RequestDtoResponseWithMD> mapToRequestDtoResponseWithMD(List<ItemRequest> itemRequests);


}
