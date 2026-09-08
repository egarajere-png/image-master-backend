package com.abcbank.images.security;

import java.util.Set;

/** Pulls the authenticated Keycloak identity (subject, username, email, name, roles) out of the current 
 * SecurityContext into a plain SecurityUser record, for services that need it without a controller passing it down explicitly. */

public record SecurityUser(
        String keycloakId,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
}