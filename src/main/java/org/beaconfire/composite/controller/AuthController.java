package org.beaconfire.composite.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beaconfire.composite.client.AuthenticationServiceClient;
import org.beaconfire.composite.dto.CreateUserRequest;
import org.beaconfire.composite.dto.UpdateUserRoleRequest;
import org.beaconfire.composite.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationServiceClient authenticationServiceClient;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUserList() {
        log.info("Received request to retrieve user list");
        return authenticationServiceClient.getUserList();
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        return authenticationServiceClient.createUser(request);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable String userId, @RequestBody UpdateUserRoleRequest request) {
        return authenticationServiceClient.updateUserRole(userId, request);
    }
}

