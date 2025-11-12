package io.studi.backend.controllers;

import io.studi.backend.config.AppInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/app")
@CrossOrigin(origins = "*")
public class AppInfoController {

    @Autowired
    private AppInfoConfig appInfoConfig;

    @GetMapping
    public Map<String, Object> getAppInfo() {
        return Map.of(
                "name", appInfoConfig.getName(),
                "version", appInfoConfig.getVersion(),
                "description", appInfoConfig.getDescription(),
                "maintainer", Map.of(
                        "name", appInfoConfig.getMaintainer().getName(),
                        "email", appInfoConfig.getMaintainer().getEmail()
                )
        );
    }
}
