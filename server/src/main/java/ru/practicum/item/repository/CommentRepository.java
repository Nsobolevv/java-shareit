package ru.practicum.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.model.Comment;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(long itemId, Sort sort);

    List<Comment> findAllByItemIdIn(List<Long> items, Sort sort);
}
