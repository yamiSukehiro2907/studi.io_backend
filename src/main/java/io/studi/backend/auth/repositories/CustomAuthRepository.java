package io.studi.backend.auth.repositories;

import io.studi.backend.auth.models.User;

public interface CustomAuthRepository {

    User createUser(User user);
}
