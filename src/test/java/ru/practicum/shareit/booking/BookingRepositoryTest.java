package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user;
    private User user1;
    private Item item;
    private Booking booking;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void create() {
        user = userRepository.save(new User(1, "email@email.com", "name"));
        user1 = userRepository.save(new User(2, "email1@email.com", "name1"));
        item = itemRepository.save(new Item(1L, user.getId(), "name", "description", true, null));
        booking = bookingRepository.save(new Booking(1L, item, user1, Status.WAITING,
                now.minusDays(1), now.plusDays(1)));
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllByBookerIdTest() {
        List<Booking> list = bookingRepository.findAllByBookerId(user1.getId(), PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByBookerIdAndStatusTest() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndStatus(user1.getId(), Status.WAITING, PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByBookerIdAndStartAfterTest() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndStartAfter(user1.getId(),
                LocalDateTime.of(2023, 7, 30, 0, 0), PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByBookerIdAndEndBeforeTest() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndEndBefore(user1.getId(),
                LocalDateTime.of(2023, 10, 30, 0, 0), PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByBookerIdAndStartBeforeAndEndAfterTest() {
        List<Booking> list = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(user1.getId(),
                now, PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByOwnerIdForItemTest() {
        List<Booking> list = bookingRepository.findAllByOwnerId(user.getId(), Sort.by(Sort.Direction.DESC, "start"));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByOwnerIdForeBookingTest() {
        List<Booking> list = bookingRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByOwnerIdAndStatusTest() {
        List<Booking> list = bookingRepository.findAllByOwnerIdAndStatus(user.getId(), Status.WAITING, PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByOwnerIdAndStartAfterTest() {
        List<Booking> list = bookingRepository.findAllByOwnerIdAndStartAfter(user.getId(),
                LocalDateTime.of(2023, 7, 30, 0, 0), PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByOwnerIdAndEndBeforeTest() {
        List<Booking> list = bookingRepository.findAllByOwnerIdAndEndBefore(user.getId(),
                LocalDateTime.of(2023, 10, 30, 0, 0), PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByOwnerIdAndStartBeforeAndEndAfterTest() {
        List<Booking> list = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(user.getId(),
                now, PageRequest.of(0, 1));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findByItemIdTest() {
        List<Booking> list = bookingRepository.findByItemId(item.getId(),
                Sort.by(Sort.Direction.DESC, "start"));
        assertEquals(List.of(booking), list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void findAllByItemIdAndBookerIdAndStatusTest() {
        Booking booking1 = bookingRepository.save(new Booking(2L, item, user1, Status.APPROVED,
                now.minusDays(1), now.plusDays(1)));
        Optional<List<Booking>> list = bookingRepository.findAllByItemIdAndBookerIdAndStatus(item.getId(), user1.getId(),
                Status.APPROVED, Sort.by(Sort.Direction.DESC, "start"));
        assertEquals(Optional.of(List.of(booking1)), list);
    }
}