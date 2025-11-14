package io.studi.backend.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @NotBlank(message = "Section title is required")
    @Field("title")
    private String title;

    @Field("resources")
    private List<Resource> resources = new ArrayList<>();
}
