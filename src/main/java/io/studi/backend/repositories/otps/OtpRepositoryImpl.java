package io.studi.backend.repositories.otps;

import io.studi.backend.models.Otp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class OtpRepositoryImpl implements OtpRepository {

    private final MongoTemplate mongoTemplate;

    public void createOtp(Otp otp) {
        otp.setExpiresAt(LocalDateTime.now());
        mongoTemplate.insert(otp, "otps");
    }

    public void deleteAll(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        mongoTemplate.findAllAndRemove(query, "otps");
    }

    public void save(Otp otp) {
        mongoTemplate.save(otp, "otps");
    }

    public Otp find(String email, String otp) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        query.addCriteria(Criteria.where("otp").is(otp));
        return mongoTemplate.findOne(query, Otp.class);
    }
}
