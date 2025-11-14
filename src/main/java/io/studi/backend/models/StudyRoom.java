package io.studi.backend.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studyrooms")
public class StudyRoom {

    @Id
    private ObjectId id;

    @NotBlank(message = "Study room name is required")
    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("isPrivate")
    private Boolean isPrivate = false;

    @Field("roomImage")
    private String roomImage = "";

    @Field("owner")
    private ObjectId ownerId;

    @Field("members")
    private List<StudyRoomMember> members;

    @Field("whiteboardState")
    private String whiteboardState = "";

    @Field("resourceHub")
    private List<Section> resourceHub = new ArrayList<>();

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;
}
