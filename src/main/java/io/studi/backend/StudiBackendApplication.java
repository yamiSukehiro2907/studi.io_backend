package io.studi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class StudiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudiBackendApplication.class, args);
    }

}

@RestController
class HealthCheck {

    @GetMapping("/health-check")
    public String healthCheck(){
        return "ok";
    }
}