package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestDtoResponse;
import ru.practicum.request.dto.ItemRequestListDto;
import ru.practicum.request.dto.RequestDtoResponseWithMD;
import ru.practicum.request.service.ItemRequestService;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createRequest(@RequestHeader(userIdHeader) Long requesterId,
                                                                @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Add request {}", itemRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.createItemRequest(itemRequestDto, requesterId));
    }

    @GetMapping
    public ResponseEntity<ItemRequestListDto> getPrivateRequests(
            @RequestHeader(userIdHeader) Long requesterId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get list all requests by user {}", requesterId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getPrivateRequests(from, size, requesterId));
    }

    @GetMapping("all")
    public ResponseEntity<ItemRequestListDto> getOtherRequests(
            @RequestHeader(userIdHeader) Long requesterId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get list all requests");
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getOtherRequests(
                        from, size,
                        requesterId));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<RequestDtoResponseWithMD> getItemRequest(
            @RequestHeader(userIdHeader) Long userId,
            @PathVariable Long requestId) {
        log.info("Get request by id {}", requestId);
        return ResponseEntity.status(HttpStatus.OK).body(itemRequestService.getItemRequest(userId, requestId));
    }
}
