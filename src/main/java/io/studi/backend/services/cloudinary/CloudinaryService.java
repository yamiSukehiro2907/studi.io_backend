package io.studi.backend.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary _cloudinary) {
        this.cloudinary = _cloudinary;
    }

    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        Map<String, String> response = new HashMap<>();
        response.put("public_id", uploadResult.get("public_id").toString());
        response.put("secure_url", uploadResult.get("secure_url").toString());
        return response;
    }

    public boolean deleteFile(String publicId) throws IOException {
        return "ok".equals(cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()).get("result"));
    }

}
