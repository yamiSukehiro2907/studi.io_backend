package io.studi.backend.repositories.user;

import io.studi.backend.models.User;

public interface UserRepository {

    void createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);

    User findByEmail(String email);

    void save(User user);
}
