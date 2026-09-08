package com.abcbank.images.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Username/password payload proxied straight through to Keycloak's token endpoint by KeycloakService 
 * — used by the login form instead of talking to Keycloak directly from the browser. */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    private String username;

    private String password;
}