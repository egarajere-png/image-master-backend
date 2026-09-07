package com.abcbank.images.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    /**
     * Returns the currently authenticated Keycloak user.
     */
    public SecurityUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "No authenticated user found"
            );
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {

            throw new IllegalStateException(
                    "Authenticated user is not using JWT authentication"
            );
        }

        var jwt = jwtAuthentication.getToken();

        return new SecurityUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                authentication.getAuthorities()
                        .stream()
                        .map(authority ->
                                authority.getAuthority()
                                        .replaceFirst("^ROLE_", "")
                        )
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Returns the Keycloak subject ID.
     */
    public String getCurrentUserId() {
        return getCurrentUser().keycloakId();
    }
}