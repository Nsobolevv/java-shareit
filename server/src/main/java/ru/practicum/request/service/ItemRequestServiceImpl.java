package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestDtoResponse;
import ru.practicum.request.dto.ItemRequestListDto;
import ru.practicum.request.dto.RequestDtoResponseWithMD;
import ru.practicum.request.mapper.ItemRequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;

import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

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
    public ItemRequestListDto getPrivateRequests(Integer from, Integer size, Long requesterId) {
        if (!users.existsById(requesterId)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", requesterId));
        }
        PageRequest pageRequest = PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return ItemRequestListDto.builder()
                .requests(mapper.mapToRequestDtoResponseWithMD(requests.findAllByRequesterId(pageRequest, requesterId)
                )).build();
    }

    @Override
    public ItemRequestListDto getOtherRequests(Integer from, Integer size, Long requesterId) {
        if (!users.existsById(requesterId)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", requesterId));
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
