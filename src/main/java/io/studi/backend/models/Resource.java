package io.studi.backend.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @NotBlank(message = "Resource title is required")
    @Field("title")
    private String title;

    @NotBlank(message = "Resource link is required")
    @Field("link")
    private String link;
}