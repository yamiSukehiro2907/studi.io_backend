package io.studi.backend.repositories.studyRoom;

import io.studi.backend.models.StudyRoom;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudyRoomRepositoryImpl implements StudyRoomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(StudyRoom studyRoom) {
        mongoTemplate.save(studyRoom, "studyrooms");
    }

    @Override
    public List<StudyRoom> findAll(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("members.user").is(new ObjectId(userId)));
        return mongoTemplate.find(query, StudyRoom.class);
    }

    @Override
    public StudyRoom findById(ObjectId roomId) {
        return mongoTemplate.findById(roomId, StudyRoom.class);
    }

    @Override
    public void deleteRoom(ObjectId roomId) {
        mongoTemplate.remove(
                Query.query(Criteria.where("_id").is(roomId)),
                StudyRoom.class
        );
    }

}
