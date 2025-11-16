package io.studi.backend.repositories.message;

import io.studi.backend.dtos.messages.MessagePageResponse;
import io.studi.backend.models.Message;
import org.bson.types.ObjectId;

public interface MessageRepository {

    MessagePageResponse getMessages(ObjectId roomId, int page, int skip);

    void save(Message message);
}
