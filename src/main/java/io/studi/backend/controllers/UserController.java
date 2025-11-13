package io.studi.backend.controllers;

import io.studi.backend.dtos.Requests.user.UpdateRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.others.UserDto;
import io.studi.backend.services.userDetails.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @ModelAttribute UpdateRequest updateRequest,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        boolean noTextData =
                (updateRequest.name() == null || updateRequest.name().isBlank()) &&
                        (updateRequest.username() == null || updateRequest.username().isBlank()) &&
                        (updateRequest.bio() == null || updateRequest.bio().isBlank()) &&
                        (updateRequest.email() == null || updateRequest.email().isBlank());

        boolean noFile = (file == null || file.isEmpty());

        if (noTextData && noFile) {
            return ResponseEntity.badRequest().body(ApiResponse.error("At least one field is required to update"));
        }

        return userService.updateUser(updateRequest, file);
    }
}
