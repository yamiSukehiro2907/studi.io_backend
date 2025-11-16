package io.studi.backend.services.users;

import io.studi.backend.dtos.Requests.user.UpdateRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.users.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    ResponseEntity<ApiResponse<UserDto>> updateUser(UpdateRequest updateRequest, MultipartFile file);

    ResponseEntity<ApiResponse<UserDto>> getProfile();

    ResponseEntity<ApiResponse<?>> changePassword(String password);

    ResponseEntity<ApiResponse<?>> changePassword2(String oldPassword, String newPassword);
}
