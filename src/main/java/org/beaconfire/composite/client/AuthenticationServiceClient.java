package org.beaconfire.composite.client;

import lombok.Data;
import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.CreateUserRequest;
import org.beaconfire.composite.dto.UpdateUserRoleRequest;
import org.beaconfire.composite.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "authentication-service", url = "${services.authentication.url}", configuration = AuthenticationServiceFeignConfig.class)
public interface AuthenticationServiceClient {

    @PostMapping("/token")
    ResponseEntity<ApiResponse<TokenResponse>> generateToken(
            @RequestBody TokenRequest request);

    @GetMapping("/secure/users")
    ResponseEntity<List<UserDto>> getUserList();

    @PostMapping("/secure/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request);

    @PutMapping("/secure/users/{userId}/role")
    ResponseEntity<UserDto> updateUserRole(@PathVariable("userId") String userId, @RequestBody UpdateUserRoleRequest request);

    @Data
    class TokenRequest {
        private String email;
        private String purpose; // e.g., "invitation", "reset-password", etc.
        private String userId; // Added userId for tracking who sends the invitation
    }

    @Data
    class TokenResponse {
        private String token;
        private String message;
        private long expiresIn; // expiration time in seconds
    }
}
