package io.studi.backend.services.messages;

import io.studi.backend.dtos.common.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface MessageService {

    ResponseEntity<ApiResponse<?>> getMessages(String roomId , int page);
}
