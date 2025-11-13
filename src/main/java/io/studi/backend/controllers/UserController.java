package io.studi.backend.controllers;


import io.studi.backend.dtos.Requests.user.UpdateRequest;
import io.studi.backend.services.userDetails.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService _userService) {
        this.userService = _userService;
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @ModelAttribute UpdateRequest updateRequest,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        if ((updateRequest.getName() == null || updateRequest.getName().isBlank()) &&
                (updateRequest.getUsername() == null || updateRequest.getUsername().isBlank()) &&
                (updateRequest.getBio() == null || updateRequest.getBio().isBlank()) &&
                (updateRequest.getEmail() == null || updateRequest.getEmail().isBlank()) &&
                (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("message", "At least one field is required to update"));
        }

        return userService.updateUser(updateRequest, file);
    }


}
