package com.tongji.codejourneycolab.codejourneycolabbackend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    @Qualifier("authorizedRoutes")
    private List<String> authorizedRoutes;

    @Autowired
    private InvalidTokenManager invalidTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // if the request is not in the authorized routes, then let it pass
        String requestURI = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean isAuthorized = authorizedRoutes.stream().anyMatch(route -> pathMatcher.match(route, requestURI));
        if (!isAuthorized) {
            filterChain.doFilter(request, response);
            return;
        }

        // check token, set authentication, and put id into request attribute
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            if(invalidTokenManager.isTokenInvalid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                Integer id = jwtTokenProvider.tryGetIdFromToken(token);
                request.setAttribute("id", id);
                System.out.println("id: " + id);
                var authentication = new UsernamePasswordAuthenticationToken(id, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
