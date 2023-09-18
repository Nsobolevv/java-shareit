package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemMapperTest {
    @Autowired
    ItemMapper itemMapper;
    private final Item item = new Item(1L, 0L, "name", "description", true, null);
    private final ItemDto itemDto1 = new ItemDto(1L, "name", "description", true, null,
            null, null, null);

    @Test
    void convertToDtoTest() {
        assertEquals(itemDto1, itemMapper.convertToDto(item));
    }

    @Test
    void convertFromDtoTest() {
        assertEquals(item, itemMapper.convertFromDto(itemDto1));
    }

}