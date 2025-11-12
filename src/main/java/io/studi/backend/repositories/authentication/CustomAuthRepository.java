package io.studi.backend.repositories.authentication;

import io.studi.backend.models.User;

public interface CustomAuthRepository {

    void createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);
}
