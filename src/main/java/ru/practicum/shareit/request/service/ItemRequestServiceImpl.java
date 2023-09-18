package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requests;
    private final UserRepository users;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User user = users.findById(requesterId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Пользователь с id %s не найден", requesterId)));
        ItemRequest newRequest = mapper.mapToItemRequest(itemRequestDto);
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now());
        return mapper.mapToItemRequestDtoResponse(requests.save(newRequest));
    }

    @Override
    public ItemRequestListDto getPrivateRequests(Integer from, Integer size, Long requesterId) throws PaginationException {
        if (!users.existsById(requesterId)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", requesterId));
        }
        if (from < 0 || size < 1) {
            throw new PaginationException("From must be positive or zero, size must be positive.");
        }
        PageRequest pageRequest = PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return ItemRequestListDto.builder()
                .requests(mapper.mapToRequestDtoResponseWithMD(requests.findAllByRequesterId(pageRequest, requesterId)
                )).build();
    }

    @Override
    public ItemRequestListDto getOtherRequests(Integer from, Integer size, Long requesterId) throws PaginationException {
        if (!users.existsById(requesterId)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", requesterId));
        }
        if (from < 0 || size < 1) {
            throw new PaginationException("From must be positive or zero, size must be positive.");
        }
        PageRequest pageRequest = PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return ItemRequestListDto.builder()
                .requests(mapper.mapToRequestDtoResponseWithMD(requests.findAllByRequesterIdNot(pageRequest, requesterId)
                )).build();
    }

    @Override
    public RequestDtoResponseWithMD getItemRequest(Long userId, Long requestId) {
        if (!users.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId));
        }
        return mapper.mapToRequestDtoResponseWithMD(
                requests.findById(requestId)
                        .orElseThrow(
                                () -> new ObjectNotFoundException(String.format("Запроса с id=%s нет", requestId)
                                )
                        ));
    }
}
