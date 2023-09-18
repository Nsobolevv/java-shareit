package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequests;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    UserMapper userMapper;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    CommentMapper commentMapper;

    private final User user = new User(1, "email@email.com", "name");
    private final User user1 = new User(2, "email1@email.com", "name1");
    private final UserDto userDto = new UserDto(1, "name", "email@email.com");
    private final UserDto user1Dto = new UserDto(2, "name1", "email1@email.com");
    private final ItemRequest itemRequest1 = new ItemRequest(1L, "request", user, LocalDateTime.now(), new HashSet<>());
    private final Item item = new Item(1L, user.getId(), "name", "description", true, itemRequest1);
    private final Item item1 = new Item(2L, user1.getId(), "name", "description", true, null);
    private final Booking lastBooking = new Booking(1L, item1, user, Status.APPROVED, LocalDateTime.of(2023, 8, 5, 0, 0),
            LocalDateTime.of(2023, 8, 30, 0, 0));
    private final Booking nextBooking = new Booking(2, item1, user, Status.APPROVED, LocalDateTime.of(2023, 10, 10, 0, 0),
            LocalDateTime.of(2023, 10, 20, 0, 0));
    private final BookingDtoShort nextBookingDtoShort = new BookingDtoShort(1L, item, 1L, LocalDateTime.of(2023, 10, 10, 0, 0),
            LocalDateTime.of(2023, 10, 20, 0, 0));
    private final BookingDtoShort lastBookingDtoShort = new BookingDtoShort(2L, item, 1L, LocalDateTime.of(2023, 8, 30, 0, 0),
            LocalDateTime.of(2023, 9, 30, 0, 0));
    private final ItemDto itemDto1 = new ItemDto(2L, "name", "description", true, null,
            null, null, new ArrayList<>());
    private final ItemDto itemDto3 = new ItemDto(1L, "name", "description", true, 1L,
            null, null, new ArrayList<>());
    private final ItemDto updateItemDto = new ItemDto(1, "updatedName", "updatedDescription", true,
            null, null, null, new ArrayList<>());
    private final ItemDto updateItemDto1 = new ItemDto(1, "updatedName", "updatedDescription", true,
            null, null, null, new ArrayList<>());
    private final Item updateItem = new Item(1L, user.getId(), "updatedName", "updatedDescription", true, itemRequest1);
    private final Comment comment = new Comment(1, "text", item, user1, LocalDateTime.of(2023, 8, 31, 0, 0));
    private final CommentDto commentDto = new CommentDto(1, "text", "name1", LocalDateTime.of(2023, 8, 31, 0, 0));
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L,
            lastBookingDtoShort, nextBookingDtoShort, List.of(commentDto));


    @Test
    void addItemTest() {
        when(itemMapper.convertFromDto(any()))
                .thenReturn(item);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRequests.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest1));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemMapper.convertToDto(any()))
                .thenReturn(itemDto);
        assertEquals(itemService.addItem(1, itemDto), itemDto);
    }

    @Test
    void addItemNotRequestIdTest() {
        when(itemMapper.convertFromDto(any()))
                .thenReturn(item1);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.save(any()))
                .thenReturn(item1);
        when(itemMapper.convertToDto(any()))
                .thenReturn(itemDto1);
        assertEquals(itemService.addItem(1, itemDto1), itemDto1);
    }

    @Test
    void addItemWrongRequestIdTest() {
        when(itemMapper.convertFromDto(any()))
                .thenReturn(item);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRequests.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException(String.format("Запроса с id=1 нет")));
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(1, itemDto));
        assertEquals(e.getMessage(), "Запроса с id=1 нет");
    }

    @Test
    void updateTest() {
        when(itemMapper.convertFromDto(any()))
                .thenReturn(updateItem);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(updateItem);
        when(itemMapper.convertToDto(any()))
                .thenReturn(updateItemDto);
        assertEquals(itemService.updateItem(1, 1, updateItemDto), updateItemDto);
    }

    @Test
    void updateWrongItemIdTest() {
        when(itemMapper.convertFromDto(any()))
                .thenReturn(updateItem);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(1, 1, updateItemDto));
        assertEquals(e.getMessage(), "Вещь с id 1 не найдена");
    }

    @Test
    void updateByNotOwnerTest() {
        when(itemMapper.convertFromDto(any()))
                .thenReturn(updateItem);
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(2, 1, updateItemDto1));
        assertEquals(e.getMessage(), "У пользователя с id 2 не найдена вещь с id 1");
    }

    @Test
    void getItemByIdTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(item))
                .thenReturn(itemDto);
        when(bookingRepository.findByItemId(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(bookingMapper.convertToDtoShort(lastBooking))
                .thenReturn(lastBookingDtoShort);
        when(bookingMapper.convertToDtoShort(nextBooking))
                .thenReturn(nextBookingDtoShort);
        when(commentRepository.findAllByItemId(anyLong(), any()))
                .thenReturn(List.of(comment));
        when(commentMapper.convertToDto(any()))
                .thenReturn(commentDto);
        assertEquals(itemService.getItemById(1, 1), itemDto);
    }

    @Test
    void getItemByIdNotOwnerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any()))
                .thenReturn(itemDto3);
        when(bookingRepository.findByItemId(anyLong(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(bookingMapper.convertToDtoShort(lastBooking))
                .thenReturn(lastBookingDtoShort);
        when(bookingMapper.convertToDtoShort(nextBooking))
                .thenReturn(nextBookingDtoShort);
        when(commentRepository.findAllByItemId(anyLong(), any()))
                .thenReturn(List.of(comment));
        when(commentMapper.convertToDto(any()))
                .thenReturn(commentDto);
        assertEquals(itemService.getItemById(1, 2), itemDto3);
    }

    @Test
    void getItemByIdWrongItemIdTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(1, 1, itemDto));
        assertEquals(e.getMessage(), "Вещь с id 1 не найдена");

    }

    @Test
    void getAllItemsTest() throws PaginationException {
        when(itemRepository.findAllByUserId(any(), anyLong()))
                .thenReturn(List.of(item));
        when(itemMapper.convertToDto(any()))
                .thenReturn(itemDto3);
        when(bookingRepository.findAllByOwnerId(anyLong(), (Sort) any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(bookingMapper.convertToDtoShort(lastBooking))
                .thenReturn(lastBookingDtoShort);
        when(bookingMapper.convertToDtoShort(nextBooking))
                .thenReturn(nextBookingDtoShort);
        when(commentRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(List.of(comment));
        when(commentMapper.convertToDto(any()))
                .thenReturn(commentDto);
        assertEquals(itemService.getAllItems(0, 10, 1L), List.of(itemDto));
    }

    @Test
    void getAllFailPaginationTest() {
        PaginationException e = assertThrows(PaginationException.class, () -> itemService.getAllItems(-1, 10, 1L));
        assertEquals(e.getMessage(), "From must be positive or zero, size must be positive.");
    }

    @Test
    void searchItemsTest() throws PaginationException {
        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(any(), anyString(), anyString()))
                .thenReturn(List.of(item));
        when(itemMapper.convertToDto(any()))
                .thenReturn(itemDto);
        assertEquals(itemService.searchItems(0, 10, "description"), List.of(itemDto));
    }

    @Test
    void searchItemsIsBlankTest() throws PaginationException {
        assertEquals(itemService.searchItems(0, 10, " "), new ArrayList<>());
    }

    @Test
    void searchItemsFailPaginationTest() {
        PaginationException e = assertThrows(PaginationException.class, () -> itemService.searchItems(-1, 10, "text"));
        assertEquals(e.getMessage(), "From must be positive or zero, size must be positive.");
    }

    @Test
    void removeItemTest() {
        itemService.removeItem(1, 1);
        verify(itemRepository).deleteById(1L);
    }


    @Test
    void addCommentTest() {
        when(commentMapper.convertFromDto(any()))
                .thenReturn(comment);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(lastBooking)));
        when(commentRepository.save(any()))
                .thenReturn(comment);
        when(commentMapper.convertToDto(any()))
                .thenReturn(commentDto);
        assertEquals(itemService.addComment(2L, 2L, commentDto), commentDto);
    }

    @Test
    void addCommentWrongItemIdTest() {
        when(commentMapper.convertFromDto(any()))
                .thenReturn(comment);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.addComment(1L, 1L, commentDto));
        assertEquals(e.getMessage(), "Вещь с id 1 не найдена");
    }

    @Test
    void addCommentFromWrongUserTest() {
        when(commentMapper.convertFromDto(any()))
                .thenReturn(comment);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.addComment(1L, 1L, commentDto));
        assertEquals(e.getMessage(), "Пользователь с id 1 не арендовал вещь с id 1.");
    }

    @Test
    void addCommentWrongDataTest() {
        when(commentMapper.convertFromDto(any()))
                .thenReturn(comment);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(nextBooking)));
        Exception e = assertThrows(ObjectNotAvailableException.class, () -> itemService.addComment(1L, 1L, commentDto));
        assertEquals(e.getMessage(), "Пользователь с id 1 не может оставлять комментарии вещи с id 1.");
    }


}