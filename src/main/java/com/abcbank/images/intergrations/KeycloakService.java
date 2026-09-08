package com.abcbank.images.intergrations;

import com.abcbank.images.domain.dto.user.TokenRequest;
import com.abcbank.images.domain.dto.user.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** Thin client for Keycloak's token endpoint — exchanges a username/password 
 * for an access/refresh token pair (password grant), used by AuthController's /auth/token proxy. */

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Authenticate a user against Keycloak using
     * username and password.
     */
    public TokenResponse getToken(TokenRequest request) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<TokenResponse> response =
                restTemplate.exchange(
                        tokenUrl,
                        HttpMethod.POST,
                        entity,
                        TokenResponse.class
                );

        return response.getBody();
    }
}