package org.beaconfire.composite.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beaconfire.composite.client.EmployeeServiceClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompositeUserController {
    private final EmployeeServiceClient employeeServiceClient;

    @GetMapping("/currentUser")
    public Map<String, Object> getCurrentUser(@RequestHeader Map<String, String> headers) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String access = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Pass headers to employee service to get profile
        EmployeeServiceClient.Employee profile = null;
        try {
            profile = employeeServiceClient.getProfile(headers).getBody().getData();
        } catch (Exception e) {
            // handle error, e.g. log or set profile to null
            log.error(e.getMessage());
        }

        Map<String, Object> result = new java.util.HashMap<>();

        if (profile != null) {
            result.put("name", (profile.getFirstName() != null ? profile.getFirstName() : "") + " " + (profile.getLastName() != null ? profile.getLastName() : ""));
            result.put("avatar", profile.getAvatarPath());
            result.put("email", profile.getEmail());
        } else {
            result.put("name", "User");
            result.put("avatar", "https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png");
            result.put("email", "employee@demo.com");
        }
        result.put("userid", userId);
        result.put("access", access);
        return result;
    }
}
