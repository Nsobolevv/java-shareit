package ru.practicum.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1")
    List<Booking> findAllByOwnerId(long ownerId, Sort sort);

    @Query(value = "select b from Booking b where b.item.userId = ?1")
    List<Booking> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatus(long ownerId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerIdAndStartAfter(long ownerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerIdAndEndBefore(long ownerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByItemId(long itemId, Sort sort);

    Optional<List<Booking>> findAllByItemIdAndBookerIdAndStatus(long itemId, long bookerId, Status status, Sort sort);
}
