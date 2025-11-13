package io.studi.backend.services.userDetails;

import io.studi.backend.dtos.Requests.user.UpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    ResponseEntity<?> updateUser(UpdateRequest updateRequest , MultipartFile file);
}
