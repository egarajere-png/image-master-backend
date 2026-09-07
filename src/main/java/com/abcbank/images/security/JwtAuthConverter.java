package com.abcbank.images.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class JwtAuthConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Set<String> roles = extractRoles(jwt);

        Collection<SimpleGrantedAuthority> authorities =
                roles.stream()
                        .map(role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role.toUpperCase()
                                )
                        )
                        .toList();

        /*
         * IMPORTANT
         *
         * authentication.getName()
         * resolves to the Keycloak subject ID.
         *
         * This matches:
         *
         * users.keycloak_id
         */
        return new JwtAuthenticationToken(
                jwt,
                authorities,
                jwt.getSubject()
        );
    }

    /**
     * Extract roles from the Keycloak JWT.
     *
     * Keycloak can place roles in:
     *
     * 1. realm_access.roles
     *
     * 2. resource_access.<client>.roles
     */
    private Set<String> extractRoles(Jwt jwt) {

        Set<String> roles = new HashSet<>();

        // ---------------------------------------------------------
        // Realm roles
        // ---------------------------------------------------------

        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        if (realmAccess != null) {

            Object realmRoles =
                    realmAccess.get("roles");

            if (realmRoles instanceof Collection<?> collection) {

                collection.forEach(role ->
                        roles.add(role.toString())
                );
            }
        }

        // ---------------------------------------------------------
        // Client roles
        // ---------------------------------------------------------

        Map<String, Object> resourceAccess =
                jwt.getClaimAsMap("resource_access");

        if (resourceAccess != null) {

            resourceAccess.values()
                    .forEach(client -> {

                        if (client instanceof Map<?, ?> clientMap) {

                            Object clientRoles =
                                    clientMap.get("roles");

                            if (clientRoles instanceof Collection<?> collection) {

                                collection.forEach(role ->
                                        roles.add(role.toString())
                                );
                            }
                        }
                    });
        }

        return roles;
    }
}