package io.studi.backend.dtos.Responses.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SenderInfo {
    private String id;
    private String name;
    private String profileImage;
}
