package io.studi.backend.dtos.Requests.user;

import lombok.Data;

@Data
public class UpdateRequest {

    private String name;

    private String email;

    private String username;

    private String bio;

}
