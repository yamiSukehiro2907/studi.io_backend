package io.studi.backend.repositories.message;

import io.studi.backend.dtos.messages.MessagePageResponse;
import io.studi.backend.models.Message;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MongoTemplate mongoTemplate;

    public MessagePageResponse getMessages(ObjectId roomId, int page, int limit) {
        int skip = (page - 1) * limit;
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("room").is(roomId)),
                Aggregation.facet(
                                Aggregation.count().as("count")
                        ).as("totalCount")
                        .and(
                                Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")),
                                Aggregation.skip(skip),
                                Aggregation.limit(limit),
                                Aggregation.lookup("users", "sender", "_id", "senderInfo"),
                                Aggregation.unwind("senderInfo", true),
                                Aggregation.sort(Sort.by(Sort.Direction.ASC, "createdAt")),
                                Aggregation.project("_id", "content", "room", "createdAt", "updatedAt")
                                        .and("senderInfo._id").as("sender._id")
                                        .and("senderInfo.name").as("sender.name")
                                        .and("senderInfo.profileImage").as("sender.profileImage")
                        ).as("messages"),
                Aggregation.project()
                        .and("messages").as("messages")
                        .and(ArrayOperators.ArrayElemAt.arrayOf("totalCount.count").elementAt(0)).as("totalMessages")
        );
        AggregationResults<MessagePageResponse> results = mongoTemplate.aggregate(aggregation, "messages", MessagePageResponse.class);
        MessagePageResponse response = results.getUniqueMappedResult();
        return response != null ? response : new MessagePageResponse();
    }

    @Override
    public void save(Message message) {
        mongoTemplate.save(message, "messages");
    }
}
