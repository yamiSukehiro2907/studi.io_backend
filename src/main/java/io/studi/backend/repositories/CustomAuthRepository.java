package io.studi.backend.repositories;

import io.studi.backend.models.User;

public interface CustomAuthRepository {

    User createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);
}
