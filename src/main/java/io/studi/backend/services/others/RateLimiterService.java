package io.studi.backend.services.others;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10)) ///  auto cleanup
            .maximumSize(10_000) ///  prevent memory leak
            .build();

    public Bucket resolveGeneralBucket(String ip) {
        return cache.get(ip + "_general", k -> createBucket(100, 100));
    }

    public Bucket resolveLoginBucket(String ip) {
        return cache.get(ip + "_login", k -> createBucket(5, 5));
    }

    public Bucket resolveSignupBucket(String ip) {
        return cache.get(ip + "_signup", k -> createBucket(2, 2));
    }

    public Bucket resolveUserBucket(String userId) {
        return cache.get("user_" + userId, k -> createBucket(100, 100));
    }

    private Bucket createBucket(int capacity, int tokens) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(tokens, Duration.ofMinutes(1)))) ///  50 requests per minute
                .build();
    }
}