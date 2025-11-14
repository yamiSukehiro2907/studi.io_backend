package io.studi.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoomMember {

    @Field("user")
    private ObjectId userId;

    @Field("isAdmin")
    private Boolean isAdmin = false;
}
