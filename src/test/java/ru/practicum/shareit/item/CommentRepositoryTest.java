package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private User user;
    private User user1;
    private ItemRequest request;
    private Item item;
    private Item item1;
    private Comment comment;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void create() {
        user = userRepository.save(new User(1L, "email@email.com", "name"));
        user1 = userRepository.save(new User(2L, "email1@email.com", "name1"));
        request = requestRepository.save(new ItemRequest(1L, "request", user, LocalDateTime.now(), new HashSet<>()));
        item = itemRepository.save(new Item(1L, user.getId(), "name1", "description", true, request));
        item1 = itemRepository.save(new Item(2L, user1.getId(), "name1", "description", true, null));
        comment = commentRepository.save(new Comment(1L, "text", item, user, LocalDateTime.of(2023, 8, 2, 0, 0)));
        comment1 = commentRepository.save(new Comment(2L, "text", item1, user1, LocalDateTime.of(2023, 8, 3, 0, 0)));
        comment2 = commentRepository.save(new Comment(3L, "text", item, user, LocalDateTime.of(2023, 8, 4, 0, 0)));
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllByItemId1Test() {
        List<Comment> list = commentRepository.findAllByItemId(item.getId(), Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(2, list.size());
        assertEquals(comment2.getId(), list.get(0).getId());
    }

    @Test
    public void findAllByItemId2Test() {
        List<Comment> list = commentRepository.findAllByItemId(item1.getId(), Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(1, list.size());
        assertEquals(comment1.getId(), list.get(0).getId());
    }

    @Test
    public void findAllByItemIdInTest() {
        List<Comment> list = commentRepository.findAllByItemIdIn(List.of(item.getId(), item1.getId()), Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(3, list.size());
        assertEquals(comment2.getId(), list.get(0).getId());
    }
}