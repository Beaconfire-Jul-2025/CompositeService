package org.beaconfire.composite.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
}

