package io.studi.backend.repositories.studyRoom;

import io.studi.backend.models.StudyRoom;

import java.util.List;

public interface StudyRoomRepository {

    void save(StudyRoom studyRoom);

    List<StudyRoom> findAll(String userId);
}
