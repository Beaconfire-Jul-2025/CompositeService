package org.beaconfire.composite.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServiceFeignConfig {
    @Value("${app.composite.secrets}")
    private String compositeServiceSecret;

    @Bean
    public RequestInterceptor authenticationHeaderInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Composite-Service-Auth", compositeServiceSecret);
        };
    }
}

