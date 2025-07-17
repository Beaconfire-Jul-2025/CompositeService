package org.beaconfire.composite.client;

import lombok.Data;
import org.beaconfire.composite.configuration.FeignForwardingInterceptor;
import org.beaconfire.composite.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "housing-service",
        url = "${services.housing.url}",
        configuration = FeignForwardingInterceptor.class)
public interface HousingServiceClient {
    @PostMapping("house/assign")
    ApiResponse<Integer> assignHouse();

}
