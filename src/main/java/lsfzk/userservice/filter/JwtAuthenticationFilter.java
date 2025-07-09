package lsfzk.userservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Read the trusted headers passed from the API Gateway
        String userId = request.getHeader("X-User-Id");
        String userRoles = request.getHeader("X-User-Roles");

        if (userId != null && !userId.isEmpty()) {
            // If the headers are present, create an authentication object
            // The gateway has already validated the JWT, so we can trust these headers.
            List<SimpleGrantedAuthority> authorities = userRoles == null || userRoles.isEmpty()
                    ? List.of()
                    : Arrays.stream(userRoles.split(","))
                    .map(role -> "ROLE_" + role) // Spring Security requires "ROLE_" prefix
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Create an authentication token. The "password" field is null as it's not needed.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // Set the authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
