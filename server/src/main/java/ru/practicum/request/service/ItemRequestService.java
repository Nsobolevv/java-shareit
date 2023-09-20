package ru.practicum.request.service;


import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestDtoResponse;
import ru.practicum.request.dto.ItemRequestListDto;
import ru.practicum.request.dto.RequestDtoResponseWithMD;


public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestListDto getPrivateRequests(Integer from, Integer size, Long requesterId);

    ItemRequestListDto getOtherRequests(Integer from, Integer size, Long requesterId);

    RequestDtoResponseWithMD getItemRequest(Long userId, Long requestId);
}
