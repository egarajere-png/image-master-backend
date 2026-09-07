package com.abcbank.images.security;

import java.util.Set;

public record KeycloakUser(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {

    public SecurityUser toSecurityUser() {
        return new SecurityUser(
                id,
                username,
                email,
                firstName,
                lastName,
                roles
        );
    }
}