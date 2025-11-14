package io.studi.backend.services.others;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.studi.backend.dtos.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public ApiResponse<Map<String, String>> uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Attempted to upload empty or null file to Cloudinary");
            return ApiResponse.error("No file provided for upload");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            Map<String, String> response = Map.of(
                    "public_id", uploadResult.get("public_id").toString(),
                    "secure_url", uploadResult.get("secure_url").toString()
            );

            log.info("File uploaded to Cloudinary successfully: {}", response.get("public_id"));
            return ApiResponse.success("File uploaded successfully", response);

        } catch (IOException e) {
            log.error("I/O error while uploading file to Cloudinary: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to upload file due to I/O error");
        } catch (Exception e) {
            log.error("Unexpected Cloudinary upload error: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to upload file to Cloudinary");
        }
    }

    public ApiResponse<Boolean> deleteFile(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            log.warn("Attempted to delete file with null or blank publicId");
            return ApiResponse.error("Public ID cannot be null or empty");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            boolean isDeleted = "ok".equals(result.get("result"));
            if (isDeleted) {
                log.info("File deleted from Cloudinary successfully: {}", publicId);
                return ApiResponse.success("File deleted successfully", true);
            } else {
                log.warn("Failed to delete file from Cloudinary: {} -> {}", publicId, result);
                return ApiResponse.error("Failed to delete file from Cloudinary");
            }

        } catch (IOException e) {
            log.error("I/O error while deleting file from Cloudinary: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to delete file due to I/O error");
        } catch (Exception e) {
            log.error("Unexpected Cloudinary deletion error: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to delete file from Cloudinary");
        }
    }
}
