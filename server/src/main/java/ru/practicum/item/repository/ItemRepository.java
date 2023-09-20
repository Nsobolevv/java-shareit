package ru.practicum.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.item.model.Item;


import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    List<Item> findAllByUserId(Pageable pageable, long userId);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(Pageable pageable,
                                                                              String name,
                                                                              String description);

    void deleteById(long itemId);
}