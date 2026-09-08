package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.action.ActionRequest;
import com.abcbank.images.domain.dto.action.ActionResponse;
import com.abcbank.images.security.AdminAccessGuard;
import com.abcbank.images.services.ActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for actions (the named workflow verbs): CRUD, admin-gated writes, open reads. */

@RestController
@RequestMapping("/api/v1/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;
    private final AdminAccessGuard adminAccessGuard;

    // ============================================================
    // CREATE (Administration department only)
    // ============================================================

    @PostMapping
    public ResponseEntity<ActionResponse> create(
            @Valid @RequestBody ActionRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(actionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(actionService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ActionResponse>> getAll() {
        return ResponseEntity.ok(actionService.getAll());
    }

    // ============================================================
    // UPDATE (Administration department only)
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ActionRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                actionService.update(id, request)
        );
    }

    // ============================================================
    // DELETE (Administration department only)
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        actionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
