package io.studi.backend.repositories.message;

import io.studi.backend.dtos.Responses.messages.MessagePageResponse;
import org.bson.types.ObjectId;

public interface MessageRepository {

    MessagePageResponse getMessages(ObjectId roomId, int page, int skip);
}
