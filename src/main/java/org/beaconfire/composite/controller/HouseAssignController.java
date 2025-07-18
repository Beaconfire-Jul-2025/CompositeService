package org.beaconfire.composite.controller;

import org.beaconfire.composite.client.HousingServiceClient;
import org.beaconfire.composite.dto.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house")
public class HouseAssignController {

    private final HousingServiceClient housingServiceClient;

    public HouseAssignController(HousingServiceClient housingServiceClient) {
        this.housingServiceClient = housingServiceClient;
    }

    @PostMapping("/assign")
    public ApiResponse<Integer> assignHouse() {
        return housingServiceClient.assignHouse(
                "999",
                "composite-system",
                "ROLE_COMPOSITE"  );
    }
}
