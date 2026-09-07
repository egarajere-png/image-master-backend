package com.abcbank.images.security;

import java.util.Set;

public record SecurityUser(
        String keycloakId,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
}