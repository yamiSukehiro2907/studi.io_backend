package io.studi.backend.repositories;

import io.studi.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

    @Override
    public User loadUserById(String userId) {
        return mongoTemplate.findById(userId, User.class);
    }

    public User loadUserByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class);
    }
}
