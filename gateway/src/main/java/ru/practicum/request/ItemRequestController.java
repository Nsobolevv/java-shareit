package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(userIdHeader) @Positive Long requesterId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getPrivateRequests(
            @RequestHeader(userIdHeader) @Positive Long requesterId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemRequestClient.getPrivateRequests(requesterId, from, size);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getOtherRequests(
            @RequestHeader(userIdHeader) @Positive Long requesterId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemRequestClient.getOtherRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(userIdHeader) @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
