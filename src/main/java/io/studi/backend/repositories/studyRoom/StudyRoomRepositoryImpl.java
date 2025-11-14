package io.studi.backend.repositories.studyRoom;

import io.studi.backend.models.StudyRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyRoomRepositoryImpl implements StudyRoomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(StudyRoom studyRoom) {
        mongoTemplate.save(studyRoom, "studyrooms");
    }
}
