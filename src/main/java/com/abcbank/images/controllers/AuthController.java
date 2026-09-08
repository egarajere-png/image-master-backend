package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.user.CurrentUserResponse;
import com.abcbank.images.domain.dto.user.TokenRequest;
import com.abcbank.images.domain.dto.user.TokenResponse;
import com.abcbank.images.intergrations.KeycloakService;
import com.abcbank.images.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/** GET /auth/me resolves (and lazily creates) the current user from their JWT and returns CurrentUserResponse — this is what AuthContext hydrates from on every page load. 
 * POST /auth/token proxies a username/password straight to Keycloak (via KeycloakService) so the login form doesn't have to talk to Keycloak directly. */

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

private final UserService userService;
private final KeycloakService keycloakService;

// ============================================================
// GET CURRENT USER
// ============================================================

@GetMapping("/me")
public ResponseEntity<CurrentUserResponse> currentUser(
        JwtAuthenticationToken authentication
) {

    var jwt = authentication.getToken();

    boolean adminRole =
            authentication.getAuthorities()
                    .stream()
                    .anyMatch(
                            authority ->
                                    "ROLE_ADMIN".equalsIgnoreCase(
                                            authority.getAuthority()
                                    )
                    );

    CurrentUserResponse response =
            userService.getCurrentUser(
                    jwt.getSubject(),
                    jwt.getClaimAsString(
                            "preferred_username"
                    ),
                    jwt.getClaimAsString(
                            "email"
                    ),
                    jwt.getClaimAsString(
                            "given_name"
                    ),
                    jwt.getClaimAsString(
                            "family_name"
                    ),
                    adminRole
            );

    return ResponseEntity.ok(response);
}

// ============================================================
// GET ACCESS TOKEN
// ============================================================

@PostMapping("/token")
public ResponseEntity<TokenResponse> token(
        @RequestBody TokenRequest request
) {

    TokenResponse response =
            keycloakService.getToken(
                    request
            );

    return ResponseEntity.ok(
            response
    );
}

}
