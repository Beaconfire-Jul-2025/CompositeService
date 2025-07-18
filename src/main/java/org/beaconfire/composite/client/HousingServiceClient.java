package org.beaconfire.composite.client;

import org.beaconfire.composite.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "housing-service", url = "${services.housing.url}")
public interface HousingServiceClient {

    @PostMapping(value = "/house/assign", headers = {
            "x-User-Id={userId}",
            "x-Username={username}",
            "x-Roles={roles}"
    })
    ApiResponse<Integer> assignHouse(
            @RequestHeader("x-User-Id") String userId,
            @RequestHeader("x-Username") String username,
            @RequestHeader("x-Roles") String roles
    );

}
