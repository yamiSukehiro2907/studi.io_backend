package io.studi.backend.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "otps")
@Data
public class Otp {

    @Id
    private String id;

    @NotNull
    private String email;

    @NotNull
    private String otp;

    @CreatedDate
    private LocalDateTime createdAt;

    @Indexed(name = "otp_ttl_index", expireAfter = "600s")
    private LocalDateTime expiresAt;
}