package io.studi.backend.auth.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    @Indexed(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Indexed(unique = true)
    private String username;

    @Field("profileImage")
    private String profileImageUrl = "";

    private String refreshToken = "";
    private String bio = "";
    private boolean verified = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean deleted = false;
    private LocalDateTime deletedAt;
}
