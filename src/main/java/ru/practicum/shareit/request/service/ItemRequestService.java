package ru.practicum.shareit.request.service;


import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestListDto getPrivateRequests(Integer from, Integer size, Long requesterId) throws PaginationException;

    ItemRequestListDto getOtherRequests(Integer from, Integer size, Long requesterId) throws PaginationException;

    RequestDtoResponseWithMD getItemRequest(Long userId, Long requestId);
}
