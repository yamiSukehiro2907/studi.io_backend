package io.studi.backend.services.userDetails;

import io.studi.backend.dtos.Requests.user.UpdateRequest;
import io.studi.backend.dtos.others.UserDto;
import io.studi.backend.helpers.LoggerHelper;
import io.studi.backend.helpers.UserHelper;
import io.studi.backend.models.User;
import io.studi.backend.repositories.userDetails.UserRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.services.cloudinary.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository _userRepository, CloudinaryService _cloudinaryService) {
        this.userRepository = _userRepository;
        this.cloudinaryService = _cloudinaryService;
    }

    @Override
    public ResponseEntity<?> updateUser(UpdateRequest updateRequest, MultipartFile file) {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = customUserDetails.getUser();

            if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
                User emailExists = userRepository.findByEmail(updateRequest.getEmail().trim());
                if (emailExists != null) {
                    return new ResponseEntity<>(Map.of("message", "Email already exists"), HttpStatus.CONFLICT);
                }
            }

            if (updateRequest.getUsername() != null && !updateRequest.getUsername().trim().isEmpty()) {
                User usernameExists = userRepository.loadUserByUsername(updateRequest.getUsername().trim());
                if (usernameExists != null) {
                    return new ResponseEntity<>(Map.of("message", "Username already exists"), HttpStatus.CONFLICT);
                }
            }

            if (updateRequest.getName() != null && !updateRequest.getName().trim().isEmpty()) {
                user.setName(updateRequest.getName().trim());
            }

            if (updateRequest.getBio() != null &&!updateRequest.getBio().trim().isEmpty()) {
                user.setBio(updateRequest.getBio().trim());
            }

            if (updateRequest.getUsername() != null && !updateRequest.getUsername().trim().isEmpty()) {
                user.setUsername(updateRequest.getUsername().trim());
            }

            if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
                user.setEmail(updateRequest.getEmail().trim());
            }

            if (file != null && !file.isEmpty()) {
                try {
                    Map<String, String> uploadMetadata = cloudinaryService.uploadFile(file);
                    String secure_url = uploadMetadata.get("secure_url");
                    user.setProfileImageUrl(secure_url);
                } catch (IOException e) {
                    LoggerHelper.error(this, "Error uploading the file: " + e.getMessage(), e);
                }
            }

            userRepository.save(user);

            UserDto updateUserDto = UserHelper.getUserDto(user);

            return new ResponseEntity<>(updateUserDto, HttpStatus.OK);
        } catch (
                Exception e) {
            LoggerHelper.error(this, "Error while updating user: " + e.getMessage(), e);
            return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
