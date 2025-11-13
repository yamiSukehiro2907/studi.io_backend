package io.studi.backend.repositories.userDetails;

import io.studi.backend.models.User;
import org.springframework.stereotype.Repository;

public interface UserRepository {

    void createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);

    User findByEmail(String email);

    void save(User user);
}
