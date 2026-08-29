package com.abhilash.rideops.filters;

import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.services.UserService;
import com.abhilash.rideops.utils.JWTUtil;
import com.abhilash.rideops.security.RestAuthenticationEntryPoint;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            JWTUtil.TokenClaims tokenClaims = jwtUtil.parseAccessToken(token);
            Long userId = tokenClaims.userId();
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("JWT authentication established: userId={}, method={}",
                        userId, request.getMethod());
            }
        } catch (JwtException | IllegalArgumentException | ResourceNotFoundException exception) {
            log.warn("Invalid or expired JWT rejected: method={}, path={}",
                    request.getMethod(), request.getRequestURI());
            authenticationEntryPoint.writeUnauthorized(request, response, "Invalid or expired access token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
