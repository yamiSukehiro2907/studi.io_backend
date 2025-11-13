package io.studi.backend.services.userDetails;

import io.studi.backend.dtos.Requests.user.UpdateRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.others.UserDto;
import io.studi.backend.helpers.UserHelper;
import io.studi.backend.models.User;
import io.studi.backend.repositories.userDetails.UserRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.services.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<UserDto>> updateUser(UpdateRequest updateRequest, MultipartFile file) {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = customUserDetails.getUser();

            if (updateRequest.email() != null && !updateRequest.email().isBlank()) {
                User emailExists = userRepository.findByEmail(updateRequest.email().trim());
                if (emailExists != null && !emailExists.getId().equals(user.getId())) {
                    log.warn("Email '{}' already exists for another user", updateRequest.email());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Email already exists"));
                }
            }

            if (updateRequest.username() != null && !updateRequest.username().isBlank()) {
                User usernameExists = userRepository.loadUserByUsername(updateRequest.username().trim());
                if (usernameExists != null && !usernameExists.getId().equals(user.getId())) {
                    log.warn("Username '{}' already exists for another user", updateRequest.username());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Username already exists"));
                }
            }

            if (updateRequest.name() != null && !updateRequest.name().isBlank()) {
                user.setName(updateRequest.name().trim());
            }

            if (updateRequest.bio() != null && !updateRequest.bio().isBlank()) {
                user.setBio(updateRequest.bio().trim());
            }

            if (updateRequest.username() != null && !updateRequest.username().isBlank()) {
                user.setUsername(updateRequest.username().trim());
            }

            if (updateRequest.email() != null && !updateRequest.email().isBlank()) {
                user.setEmail(updateRequest.email().trim());
            }

            if (file != null && !file.isEmpty()) {
                ApiResponse<Map<String, String>> uploadResponse = cloudinaryService.uploadFile(file);

                if (!uploadResponse.success()) {
                    log.warn("Cloudinary upload failed for user {}: {}", user.getId(), uploadResponse.message());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error(uploadResponse.message()));
                }

                Map<String, String> uploadData = uploadResponse.data();
                if (uploadData != null && uploadData.containsKey("secure_url")) {
                    String secureUrl = uploadData.get("secure_url");
                    user.setProfileImageUrl(secureUrl);
                    log.info("User {} uploaded a new profile image: {}", user.getId(), secureUrl);
                } else {
                    log.warn("Cloudinary response missing secure_url for user {}", user.getId());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error("Failed to retrieve image URL after upload"));
                }
            }


            userRepository.save(user);
            UserDto updatedUserDto = UserHelper.getUserDto(user);

            log.info("User {} updated successfully", user.getId());
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUserDto));

        } catch (Exception e) {
            log.error("Unexpected error while updating user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ApiResponse.success("User details!", UserHelper.getUserDto(userDetails.getUser())));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> changePassword(String password) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully!"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> changePassword2(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword)) {
            return new ResponseEntity<>(ApiResponse.error("New password should be different from old Password"), HttpStatus.CONFLICT);
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Old Password does not matched!"));
        }
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully!"));
    }
}
