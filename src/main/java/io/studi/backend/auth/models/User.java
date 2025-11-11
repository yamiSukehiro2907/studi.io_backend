package io.studi.backend.auth.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;
    private String name;
    private String email;

    private String password;
    private String username;

    @Field("profileImage")
    private String profileImageUrl;

    private String refreshToken;
    private String bio;
    private boolean verified;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean deleted = false;
    private LocalDateTime deletedAt;
}
