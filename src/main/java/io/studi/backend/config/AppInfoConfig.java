package io.studi.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppInfoConfig {

    private String name;
    private String version;
    private String description;
    private Maintainer maintainer = new Maintainer();

    @Data
    public static class Maintainer {
        private String name;
        private String email;
    }
}
