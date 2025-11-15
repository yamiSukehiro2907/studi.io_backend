package io.studi.backend.repositories.studyRoom;

import io.studi.backend.models.StudyRoom;
import org.bson.types.ObjectId;

import java.util.List;

public interface StudyRoomRepository {

    void save(StudyRoom studyRoom);

    List<StudyRoom> findAll(String userId);

    StudyRoom findById(ObjectId roomId);
}
