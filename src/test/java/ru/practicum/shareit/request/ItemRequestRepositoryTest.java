package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    @BeforeEach
    void createUserAndItemRequest() {
        user = userRepository.save(new User(1, "name", "email@email.com"));
        user2 = userRepository.save(new User(2, "name1", "email1@email.com"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L, "description", user,
                LocalDateTime.of(2023, 9, 1, 0, 0), new HashSet<>()));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L, "description", user2,
                LocalDateTime.of(2023, 9, 1, 0, 0), new HashSet<>()));
        itemRequest3 = itemRequestRepository.save(new ItemRequest(3L, "description", user2,
                LocalDateTime.of(2023, 9, 1, 0, 0), new HashSet<>()));

    }

    @AfterEach
    void delete() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllByRequesterId1Test() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequesterId(PageRequest.of(0, 2), user.getId());
        assertEquals(1, list.size());
        assertEquals(itemRequest.getId(), list.get(0).getId());
    }

    @Test
    public void findAllByRequesterId2Test() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequesterId(PageRequest.of(0, 2), user2.getId());
        assertEquals(2, list.size());
        assertEquals(itemRequest3.getId(), list.get(1).getId());
    }


    @Test
    public void findAllByRequesterId1NotTest() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequesterIdNot(PageRequest.of(0, 2), user.getId());
        assertEquals(2, list.size());
        assertEquals(itemRequest2.getId(), list.get(0).getId());
    }

    @Test
    public void findAllByRequesterId2NotTest() {
        List<ItemRequest> list = itemRequestRepository.findAllByRequesterIdNot(PageRequest.of(0, 2), user2.getId());
        assertEquals(1, list.size());
        assertEquals(itemRequest.getId(), list.get(0).getId());
    }

}