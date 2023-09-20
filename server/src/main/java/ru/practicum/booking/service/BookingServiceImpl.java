package ru.practicum.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingInputDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.AccessLevel;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.State;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.AccessException;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.ObjectNotAvailableException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.logger.Logger;
import ru.practicum.user.UserService;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final String host = "localhost";
    private final String port = "8080";
    private final String protocol = "http";

    @Transactional
    @Override
    public BookingDto addBooking(long bookerId, BookingInputDto bookingInputDto) {
        Booking booking = bookingMapper.convertFromDto(bookingInputDto);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/bookings")
                .build();
        Logger.logInfo(HttpMethod.POST, uriComponents.toUriString(), booking.toString());
        User booker = userMapper.convertFromDto(userService.getUserById(bookerId));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Вещь с id %s не найдена", booking.getItem().getId())));
        validateAddBooking(bookerId, booking, item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        Booking bookingSaved = bookingRepository.save(booking);
        Logger.logSave(HttpMethod.POST, uriComponents.toUriString(), bookingSaved.toString());
        return bookingMapper.convertToDto(bookingSaved);
    }

    @Transactional
    @Override
    public BookingDto approveOrRejectBooking(long ownerId, long bookingId, boolean approved, AccessLevel accessLevel) {
        User owner = userMapper.convertFromDto(userService.getUserById(ownerId));
        Booking booking = getBookingById(bookingId, owner.getId(), accessLevel);
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new InvalidDataException(String.format("У бронирования с id %d уже стоит статус %s",
                    bookingId, Status.APPROVED.name()));
        }
        booking.setStatus((approved ? Status.APPROVED : Status.REJECTED));
        Booking bookingSaved = bookingRepository.save(booking);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/bookings/")
                .query("approved={approved}")
                .build();
        Logger.logSave(HttpMethod.PATCH, uriComponents.toUriString(), bookingSaved.toString());
        return bookingMapper.convertToDto(bookingSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getBookingById(long bookingId, long userId, AccessLevel accessLevel) {
        User user = userMapper.convertFromDto(userService.getUserById(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        if (isUnableToAccess(user.getId(), booking, accessLevel)) {
            throw new AccessException(String.format("У пользователя с id %d нет прав на просмотр бронирования с id %d",
                    userId, bookingId));
        }
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/bookings/{bookingId}")
                .build();
        Logger.logSave(HttpMethod.GET, uriComponents.toUriString(), booking.toString());
        return booking;
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBooking(long bookingId, long userId, AccessLevel accessLevel) {
        User user = userMapper.convertFromDto(userService.getUserById(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        if (isUnableToAccess(user.getId(), booking, accessLevel)) {
            throw new AccessException(String.format("У пользователя с id %d нет прав на просмотр бронирования с id %d",
                    userId, bookingId));
        }
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/bookings/{bookingId}")
                .build();
        Logger.logSave(HttpMethod.GET, uriComponents.toUriString(), booking.toString());
        return bookingMapper.convertToDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsOfCurrentUser(State state, long bookerId, Integer from, Integer size) {
        User booker = userMapper.convertFromDto(userService.getUserById(bookerId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        Status.REJECTED, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(),
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(),
                        LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(),
                        LocalDateTime.now(), pageable);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(booker.getId(), pageable);
        }
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/bookings/")
                .query("state={state}")
                .build();
        Logger.logSave(HttpMethod.GET, uriComponents.toUriString(), bookings.toString());
        return bookings
                .stream()
                .map(bookingMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsOfOwner(State state, long ownerId, Integer from, Integer size) {
        User owner = userMapper.convertFromDto(userService.getUserById(ownerId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        Status.REJECTED, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(owner.getId(),
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(owner.getId(),
                        LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                        LocalDateTime.now(), pageable);
                break;
            default:
                bookings = bookingRepository.findAllByOwnerId(owner.getId(), pageable);
        }
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path("/bookings/owner")
                .query("state={state}")
                .build();
        Logger.logSave(HttpMethod.GET, uriComponents.toUriString(), bookings.toString());
        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .collect(Collectors.toList());
    }

    private boolean isNotValidDate(LocalDateTime startBooking, LocalDateTime endBooking) {
        return startBooking.isBefore(LocalDateTime.now()) || endBooking.isBefore(LocalDateTime.now())
                || endBooking.isBefore(startBooking) || startBooking.equals(endBooking);
    }

    private boolean isUnableToAccess(long userId, Booking booking, AccessLevel accessLevel) {
        switch (accessLevel) {
            case OWNER:
                return booking.getItem().getUserId() != userId;
            case BOOKER:
                return booking.getBooker().getId() != userId;
            case OWNER_AND_BOOKER:
                return !(booking.getItem().getUserId() == userId || booking.getBooker().getId() == userId);
            default:
                throw new AccessException("Неизвестный уровень допуска");
        }
    }

    private void validateAddBooking(long bookerId, Booking booking, Item item) {
        if (bookerId == item.getUserId()) {
            throw new AccessException("Владелец вещи не может бронировать свои вещи.");
        }
        if (!item.getAvailable()) {
            throw new ObjectNotAvailableException(String.format("Вещь с id %d не доступна для бронирования.",
                    item.getId()));
        }
        if (isNotValidDate(booking.getStart(), booking.getEnd())) {
            throw new InvalidDataException("Даты бронирования выбраны некорректно.");
        }
    }
}
