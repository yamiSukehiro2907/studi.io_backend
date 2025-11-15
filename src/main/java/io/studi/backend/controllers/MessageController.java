package io.studi.backend.controllers;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.services.messages.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getMessages(@PathVariable("id") String roomId, @RequestParam(defaultValue = "1") int page) {
        if (roomId == null || roomId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Room Id is required"));
        }
        page = Math.max(1, page);
        return messageService.getMessages(roomId, page);
    }
}
