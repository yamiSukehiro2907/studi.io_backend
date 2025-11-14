package io.studi.backend.repositories.user;

import io.studi.backend.models.User;
import org.bson.types.ObjectId;

import java.util.List;

public interface UserRepository {

    void createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);

    User findByEmail(String email);

    void save(User user);

    User findById(ObjectId userId);

    List<User> findAllById(List<ObjectId> ids);
}
