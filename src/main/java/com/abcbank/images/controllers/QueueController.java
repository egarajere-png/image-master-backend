package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.queue.QueueActionResponse;
import com.abcbank.images.domain.dto.queue.QueueRequest;
import com.abcbank.images.domain.dto.queue.QueueResponse;
import com.abcbank.images.domain.dto.user.UserResponse;
import com.abcbank.images.domain.entities.User;
// import com.abcbank.images.mappers.UserMapper;
import com.abcbank.images.security.AdminAccessGuard;
import com.abcbank.images.services.QueueService;
import com.abcbank.images.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for queues: CRUD (admin-only writes), queue-user
 * membership, and queue-action wiring. GET /queues/active is the
 * one non-admin endpoint that matters most — it's what "My Queue"
 * calls, and it's department-filtered via
 * QueueService.getAccessibleQueues(), which only works correctly
 * now that queues actually persist a department (migration V14).
 */

@RestController
@RequestMapping("/api/v1/queues")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;
    private final UserService userService;
    private final AdminAccessGuard adminAccessGuard;

    // ============================================================
    // CREATE (Administration department only)
    // ============================================================

    @PostMapping
    public ResponseEntity<QueueResponse> create(
            @Valid @RequestBody QueueRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(queueService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueueResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(queueService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<QueueResponse>> getAll() {
        return ResponseEntity.ok(queueService.getAll());
    }

    /**
     * Get active queues filtered by department.
     * - Admins (is_admin=true) see all active queues
     * - Other users see active queues from their department only
     */
    @GetMapping("/active")
    public ResponseEntity<List<QueueResponse>> getActive(
            JwtAuthenticationToken authentication
    ) {
        User currentUser = userService.getByKeycloakId(
                authentication.getName()
        );
        
        List<QueueResponse> queues = queueService.getAccessibleQueues(
                currentUser
        );
        
        return ResponseEntity.ok(queues);
    }

    // ============================================================
    // UPDATE (Administration department only)
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<QueueResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody QueueRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                queueService.update(id, request)
        );
    }

    // ============================================================
    // ACTIVATE (Administration department only)
    // ============================================================

    @PatchMapping("/{id}/activate")
    public ResponseEntity<QueueResponse> activate(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                queueService.activate(id)
        );
    }

    // ============================================================
    // DEACTIVATE (Administration department only)
    // ============================================================

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<QueueResponse> deactivate(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                queueService.deactivate(id)
        );
    }

    // ============================================================
    // ASSIGN USER (Administration department only)
    // ============================================================

    @PutMapping("/{queueId}/users/{userId}")
    public ResponseEntity<Void> assignUser(
            @PathVariable Long queueId,
            @PathVariable Long userId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        queueService.assignUser(queueId, userId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // REMOVE USER (Administration department only)
    // ============================================================

    @DeleteMapping("/{queueId}/users/{userId}")
    public ResponseEntity<Void> removeUser(
            @PathVariable Long queueId,
            @PathVariable Long userId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        queueService.removeUser(queueId, userId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // GET USERS IN QUEUE — stays open: AuthContext resolves every
    // authenticated user's own queue membership through this.
    // ============================================================

    @GetMapping("/{queueId}/users")
public ResponseEntity<List<UserResponse>> getUsers(
        @PathVariable Long queueId
) {
    return ResponseEntity.ok(
            queueService.getUsers(queueId)
    );
}

    // ============================================================
    // ASSIGN ACTION (Administration department only)
    // ============================================================

    @PutMapping("/{queueId}/actions/{actionId}")
    public ResponseEntity<QueueActionResponse> assignAction(
            @PathVariable Long queueId,
            @PathVariable Long actionId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                queueService.assignAction(queueId, actionId)
        );
    }

    // ============================================================
    // REMOVE ACTION (Administration department only)
    // ============================================================

    @DeleteMapping("/{queueId}/actions/{actionId}")
    public ResponseEntity<Void> removeAction(
            @PathVariable Long queueId,
            @PathVariable Long actionId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        queueService.removeAction(queueId, actionId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // GET ACTIONS FOR QUEUE — stays open: ActionPanel/TransitionForm
    // depend on this for every authenticated user.
    // ============================================================

    @GetMapping("/{queueId}/actions")
    public ResponseEntity<List<QueueActionResponse>> getActions(
            @PathVariable Long queueId
    ) {
        return ResponseEntity.ok(
                queueService.getActions(queueId)
        );
    }
}
