package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private Item item;
    private Item item2;
    private Item item3;
    private User user;
    private User user1;
    private ItemRequest request;

    @BeforeEach
    void create() {
        user = userRepository.save(new User(1, "email@email.com", "name"));
        user1 = userRepository.save(new User(2, "email1@email.com", "name1"));
        request = requestRepository.save(new ItemRequest(1L, "request", user, LocalDateTime.now(), new HashSet<>()));
        item = itemRepository.save(new Item(1L, user.getId(), "name1", "description", true, request));
        item2 = itemRepository.save(new Item(2L, user1.getId(), "name", "description", true, request));
        item3 = itemRepository.save(new Item(3L, user1.getId(), "abc", "abcde", true, request));
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllByUserIdTest() {
        List<Item> list = itemRepository.findAllByUserId(PageRequest.of(0, 2), user.getId());
        assertEquals(1, list.size());
        assertEquals(item.getId(), list.get(0).getId());
    }

    @Test
    public void findAllByUserId2ItemTest() {
        List<Item> list = itemRepository.findAllByUserId(PageRequest.of(0, 2), user1.getId());
        assertEquals(2, list.size());
        assertEquals(item3.getId(), list.get(1).getId());
    }

    @Test
    public void findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrueDescriptionTest() {
        List<Item> list = itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(PageRequest.of(0, 3), "description", "description");
        assertEquals(2, list.size());
        assertEquals(item2.getId(), list.get(1).getId());
    }

    @Test
    public void findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrueNameTest() {
        List<Item> list = itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(PageRequest.of(0, 3), "abc", "abc");
        assertEquals(1, list.size());
        assertEquals(item3.getId(), list.get(0).getId());
    }

    @Test
    public void findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrueNameDescriptionTest() {
        List<Item> list = itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(PageRequest.of(0, 3), "abc", "description");
        assertEquals(3, list.size());
    }

    @Test
    public void deleteByIdTest() {
        assertEquals(Optional.of(item), itemRepository.findById(item.getId()));
        itemRepository.deleteById(item.getId());
        assertEquals(Optional.empty(), itemRepository.findById(1L));
    }
}