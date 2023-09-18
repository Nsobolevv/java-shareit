package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestMapperTest {
    @Autowired
    ItemRequestMapper itemRequestMapper;

    private final ItemRequest itemRequest = new ItemRequest(1L, "description", null, null, new HashSet<>());
    private final ItemRequestDto itemRequestDto = new ItemRequestDto("description");
    private final ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description", null);
    private final ItemDataForRequestDto itemDataForRequestDto = new ItemDataForRequestDto(1L, "name", "description", true, null);
    private final Item item = new Item(1L, 1L, "name", "description", true, null);
    private final RequestDtoResponseWithMD requestDtoResponseWithMD = new RequestDtoResponseWithMD(1L, "description", null, new HashSet<>());


    @Test
    void mapToItemRequestDtoResponseTest() {
        assertEquals(itemRequestDtoResponse, itemRequestMapper.mapToItemRequestDtoResponse(itemRequest));
    }

    @Test
    void mapToItemDataForRequestDtoTest() {
        assertEquals(itemDataForRequestDto, itemRequestMapper.mapToItemDataForRequestDto(item));
    }

    @Test
    void mapToRequestDtoResponseWithMDTest() {
        assertEquals(requestDtoResponseWithMD, itemRequestMapper.mapToRequestDtoResponseWithMD(itemRequest));
    }

    @Test
    void mapToRequestDtoResponseWithMDOtherTest() {
        assertEquals(List.of(requestDtoResponseWithMD), itemRequestMapper.mapToRequestDtoResponseWithMD(List.of(itemRequest)));
    }


}