package io.studi.backend.auth.repositories;

import io.studi.backend.auth.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CustomAuthRepositoryImpl implements CustomAuthRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomAuthRepositoryImpl(MongoTemplate _mongoTemplate) {
        this.mongoTemplate = _mongoTemplate;
    }

    @Override
    public User createUser(User user) {
        return mongoTemplate.insert(user, "users");
    }
}
