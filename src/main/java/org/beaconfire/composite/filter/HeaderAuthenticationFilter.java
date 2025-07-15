package org.beaconfire.composite.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
  @Override
  protected boolean shouldNotFilter(javax.servlet.http.HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/actuator/");
  }

  @Override
  protected void doFilterInternal(
      javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response,
      javax.servlet.FilterChain filterChain)
      throws javax.servlet.ServletException, java.io.IOException {
    String userId = request.getHeader("x-User-Id");
    String username = request.getHeader("x-Username");
    String rolesHeader = request.getHeader("x-Roles");

    log.info("userId = {}, username = {}, roles = {}", userId, username, rolesHeader);

    if (userId != null && username != null && rolesHeader != null) {
      List<GrantedAuthority> authorities =
          Arrays.stream(rolesHeader.split(","))
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(userId, null, authorities);
      auth.setDetails(username);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    } else {
      response.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
  }
}
