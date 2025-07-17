package org.beaconfire.composite.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignForwardingInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (request != null) {
            String userId = request.getHeader("x-User-Id");
            String username = request.getHeader("x-Username");
            String roles = request.getHeader("x-Roles");

            if (userId != null) template.header("x-User-Id", userId);
            if (username != null) template.header("x-Username", username);
            if (roles != null) template.header("x-Roles", roles);
        }
    }
}
