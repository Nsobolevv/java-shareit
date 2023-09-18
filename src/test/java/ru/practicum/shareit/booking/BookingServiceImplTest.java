package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    UserMapper userMapper;
    @Mock
    BookingMapper bookingMapper;

    private final User user = new User(1, "email@email.com", "name");
    private final User user1 = new User(2, "email1@email.com", "name1");
    private final UserDto userDto = new UserDto(1, "email@email.com", "name");
    private final UserDto user1Dto = new UserDto(2, "email1@email.com", "name1");
    private final ItemRequest itemRequest1 = new ItemRequest(1L, "request", user, LocalDateTime.now(), new HashSet<>());
    private final Item item = new Item(1L, user.getId(), "name", "description", true, itemRequest1);
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, null,
            null, null, new ArrayList<>());
    private final Booking booking = new Booking(1L, item, user1, Status.WAITING,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final Booking bookingApproved = new Booking(1, item, user1, Status.APPROVED,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final BookingInputDto bookingInputDto = new BookingInputDto(1L, 1L,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));
    private final BookingDto bookingDto = new BookingDto(1L, itemDto, userDto, Status.WAITING,
            LocalDateTime.of(2023, 9, 30, 0, 0),
            LocalDateTime.of(2023, 10, 30, 0, 0));


    @Test
    void addBookingTest() {
        when(bookingMapper.convertFromDto(any()))
                .thenReturn(booking);
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.addBooking(2, bookingInputDto), bookingDto);
    }

    @Test
    void addBookingNotItemTest() {
        when(bookingMapper.convertFromDto(any()))
                .thenReturn(booking);
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> bookingService.addBooking(2, bookingInputDto));
        assertEquals(e.getMessage(), "Вещь с id 1 не найдена");
    }

    @Test
    void addBookingBadValidationIdUserTest() {
        when(bookingMapper.convertFromDto(any()))
                .thenReturn(booking);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception e = assertThrows(AccessException.class, () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals(e.getMessage(), "Владелец вещи не может бронировать свои вещи.");
    }

    @Test
    void addBookingBadValidationItemTest() {
        Item itemFl = new Item(1L, user.getId(), "name", "description", false, itemRequest1);
        Booking bookingFl = new Booking(1L, itemFl, user1, Status.WAITING,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(bookingMapper.convertFromDto(any()))
                .thenReturn(bookingFl);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemFl));
        Exception e = assertThrows(ObjectNotAvailableException.class, () -> bookingService.addBooking(2, bookingInputDto));
        assertEquals(e.getMessage(), "Вещь с id 1 не доступна для бронирования.");
    }

    @Test
    void addBookingBadValidationDataTest() {
        Booking bookingFl = new Booking(1L, item, user1, Status.WAITING,
                LocalDateTime.of(2023, 6, 30, 0, 0),
                LocalDateTime.of(2023, 7, 30, 0, 0));
        when(bookingMapper.convertFromDto(any()))
                .thenReturn(bookingFl);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception e = assertThrows(InvalidDataException.class, () -> bookingService.addBooking(2, bookingInputDto));
        assertEquals(e.getMessage(), "Даты бронирования выбраны некорректно.");
    }

    @Test
    void approveBookingTest() {
        BookingDto bookingDtoApr = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(bookingApproved);
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoApr);
        assertEquals(bookingService.approveOrRejectBooking(1L, 1L, true, AccessLevel.OWNER), bookingDtoApr);
    }

    @Test
    void rejectBookingTest() {
        BookingDto bookingDtoRJ = new BookingDto(1L, itemDto, userDto, Status.REJECTED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        Booking bookingRJ = new Booking(1, item, user1, Status.REJECTED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(bookingRJ);
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoRJ);
        assertEquals(bookingService.approveOrRejectBooking(1L, 1L, false, AccessLevel.OWNER), bookingDtoRJ);
    }

    @Test
    void approveOrRejectBookingWrongApprovedTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingApproved));
        Exception e = assertThrows(InvalidDataException.class, () -> bookingService.approveOrRejectBooking(1L, 1L, true, AccessLevel.OWNER));
        assertEquals(e.getMessage(), "У бронирования с id 1 уже стоит статус APPROVED");
    }

    @Test
    void getBookingByIdOwnerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertEquals(bookingService.getBookingById(1L, 1L, AccessLevel.OWNER), booking);
    }

    @Test
    void getBookingByIdBookerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertEquals(bookingService.getBookingById(1L, 2L, AccessLevel.BOOKER), booking);
    }

    @Test
    void getBookingByIdOwnerAndBookerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertEquals(bookingService.getBookingById(1L, 1L, AccessLevel.OWNER_AND_BOOKER), booking);
    }

    @Test
    void getBookingByIdOtherTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception e = assertThrows(AccessException.class, () -> bookingService.getBookingById(1L, 1L, AccessLevel.OTHER));
        assertEquals(e.getMessage(), "Неизвестный уровень допуска");
    }

    @Test
    void getBookingByIdWrongBookingIdTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingById(1L, 2L, AccessLevel.OWNER));
        assertEquals(e.getMessage(), "Бронирование с id 1 не найдено");
    }

    @Test
    void getBookingByIdWrongUserIdTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(AccessException.class, () -> bookingService.getBookingById(1L, 2L, AccessLevel.OWNER));
        assertEquals(e.getMessage(), "У пользователя с id 2 нет прав на просмотр бронирования с id 1");
    }

    @Test
    void getBookingOwnerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBooking(1L, 1L, AccessLevel.OWNER), bookingDto);
    }

    @Test
    void getBookingBookerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBooking(1L, 2L, AccessLevel.BOOKER), bookingDto);
    }

    @Test
    void getBookingOwnerAndBookerTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBooking(1L, 1L, AccessLevel.OWNER_AND_BOOKER), bookingDto);
    }

    @Test
    void getBookingOtherTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(AccessException.class, () -> bookingService.getBooking(1L, 1L, AccessLevel.OTHER));
        assertEquals(e.getMessage(), "Неизвестный уровень допуска");
    }

    @Test
    void getBookingWrongBookingIdTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(1L, 2L, AccessLevel.OWNER));
        assertEquals(e.getMessage(), "Бронирование с id 1 не найдено");
    }

    @Test
    void getBookingWrongUserIdTest() {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user1);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(AccessException.class, () -> bookingService.getBookingById(1L, 2L, AccessLevel.OWNER));
        assertEquals(e.getMessage(), "У пользователя с id 2 нет прав на просмотр бронирования с id 1");
    }

    @Test
    void getBookingsOfCurrentUserWAITINGTest() throws PaginationException {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBookingsOfCurrentUser(State.WAITING, 1L, 0, 1), List.of(bookingDto));
    }

    @Test
    void getBookingsOfCurrentUserREJECTEDTest() throws PaginationException {
        BookingDto bookingDtoRJ = new BookingDto(1L, itemDto, userDto, Status.REJECTED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        Booking bookingRJ = new Booking(1, item, user, Status.REJECTED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(bookingRJ));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoRJ);
        assertEquals(bookingService.getBookingsOfCurrentUser(State.REJECTED, 1L, 0, 1), List.of(bookingDtoRJ));
    }

    @Test
    void getBookingsOfCurrentUserPASTTest() throws PaginationException {
        BookingDto bookingDtoPast = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                LocalDateTime.of(2023, 6, 30, 0, 0),
                LocalDateTime.of(2023, 7, 30, 0, 0));
        Booking bookingPast = new Booking(1, item, user, Status.APPROVED,
                LocalDateTime.of(2023, 6, 30, 0, 0),
                LocalDateTime.of(2023, 7, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(List.of(bookingPast));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoPast);
        assertEquals(bookingService.getBookingsOfCurrentUser(State.PAST, 1L, 0, 1), List.of(bookingDtoPast));
    }

    @Test
    void getBookingsOfCurrentUserFUTURETest() throws PaginationException {
        BookingDto bookingDtoFuture = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        Booking bookingFuture = new Booking(1, item, user, Status.APPROVED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(List.of(bookingFuture));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoFuture);
        assertEquals(bookingService.getBookingsOfCurrentUser(State.FUTURE, 1L, 0, 1), List.of(bookingDtoFuture));
    }

    @Test
    void getBookingsOfCurrentUserCURRENTTest() throws PaginationException {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDtoCurrent = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                now.minusDays(1),
                now.plusDays(1));
        Booking bookingCurrent = new Booking(1, item, user, Status.APPROVED,
                now.minusDays(1),
                now.plusDays(1));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any()))
                .thenReturn(List.of(bookingCurrent));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoCurrent);
        assertEquals(bookingService.getBookingsOfCurrentUser(State.CURRENT, 1L, 0, 1), List.of(bookingDtoCurrent));
    }

    @Test
    void getBookingsOfCurrentUserAllTest() throws PaginationException {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerId(anyLong(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBookingsOfCurrentUser(State.ALL, 1L, 0, 1), List.of(bookingDto));
    }

    @Test
    void getBookingsOfCurrentUserFailPaginationTest() {
        PaginationException e = assertThrows(PaginationException.class, () -> bookingService.getBookingsOfCurrentUser(State.ALL, 1L, -1, 1));
        assertEquals(e.getMessage(), "From must be positive or zero, size must be positive.");
    }

    @Test
    void getBookingsOfOwnerWAITINGTest() throws PaginationException {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBookingsOfOwner(State.WAITING, 1L, 0, 1), List.of(bookingDto));
    }

    @Test
    void getBookingsOfOwnerREJECTEDTest() throws PaginationException {
        BookingDto bookingDtoRJ = new BookingDto(1L, itemDto, userDto, Status.REJECTED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        Booking bookingRJ = new Booking(1, item, user, Status.REJECTED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(bookingRJ));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoRJ);
        assertEquals(bookingService.getBookingsOfOwner(State.REJECTED, 1L, 0, 1), List.of(bookingDtoRJ));
    }

    @Test
    void getBookingsOfOwnerUserPASTTest() throws PaginationException {
        BookingDto bookingDtoPast = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                LocalDateTime.of(2023, 6, 30, 0, 0),
                LocalDateTime.of(2023, 7, 30, 0, 0));
        Booking bookingPast = new Booking(1, item, user, Status.APPROVED,
                LocalDateTime.of(2023, 6, 30, 0, 0),
                LocalDateTime.of(2023, 7, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(List.of(bookingPast));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoPast);
        assertEquals(bookingService.getBookingsOfOwner(State.PAST, 1L, 0, 1), List.of(bookingDtoPast));
    }

    @Test
    void getBookingsOfOwnerFUTURETest() throws PaginationException {
        BookingDto bookingDtoFuture = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        Booking bookingFuture = new Booking(1, item, user, Status.APPROVED,
                LocalDateTime.of(2023, 9, 30, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(List.of(bookingFuture));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoFuture);
        assertEquals(bookingService.getBookingsOfOwner(State.FUTURE, 1L, 0, 1), List.of(bookingDtoFuture));
    }

    @Test
    void getBookingsOfOwnerCURRENTTest() throws PaginationException {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDtoCurrent = new BookingDto(1L, itemDto, userDto, Status.APPROVED,
                now.minusDays(1),
                now.plusDays(1));
        Booking bookingCurrent = new Booking(1, item, user, Status.APPROVED,
                now.minusDays(1),
                now.plusDays(1));
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any()))
                .thenReturn(List.of(bookingCurrent));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDtoCurrent);
        assertEquals(bookingService.getBookingsOfOwner(State.CURRENT, 1L, 0, 1), List.of(bookingDtoCurrent));
    }

    @Test
    void getBookingsOfOwnerAllTest() throws PaginationException {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(userMapper.convertFromDto(any()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerId(anyLong(), (Pageable) any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);
        assertEquals(bookingService.getBookingsOfOwner(State.ALL, 1L, 0, 1), List.of(bookingDto));
    }

    @Test
    void getBookingsOfOwnerFailPaginationTest() {
        PaginationException e = assertThrows(PaginationException.class, () -> bookingService.getBookingsOfOwner(State.ALL, 1L, 1, 0));
        assertEquals(e.getMessage(), "From must be positive or zero, size must be positive.");
    }
}