package io.studi.backend.auth.repositories;

import io.studi.backend.auth.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends MongoRepository<User, String>, CustomAuthRepository {

    Optional<User> findByEmail(String email);


}
