package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.transition.TransitionRequest;
import com.abcbank.images.domain.dto.transition.TransitionResponse;
import com.abcbank.images.security.AdminAccessGuard;
import com.abcbank.images.services.TransitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;



/**
 * NOTE: this duplicates WorkflowController's /api/v1/workflow/transitions
 * endpoints at a different base path. Nothing in the frontend calls
 * this controller — it's guarded entirely (reads included) since it
 * has no known non-admin consumer. Worth deciding whether to keep
 * both or consolidate on one; I haven't removed either since I can't
 * be sure nothing else depends on this exact path.
 */
@RestController
@RequestMapping("/api/v1/transitions")
@RequiredArgsConstructor
public class TransitionController {

    private final TransitionService transitionService;
    private final AdminAccessGuard adminAccessGuard;


    // ============================================================
    // CREATE (Administration department only)
    // ============================================================

    @PostMapping
    public ResponseEntity<TransitionResponse> create(
            @Valid @RequestBody TransitionRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        transitionService.create(request)
                );
    }


    // ============================================================
    // GET BY ID (Administration department only)
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<TransitionResponse> getById(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                transitionService.getById(id)
        );
    }


    // ============================================================
    // GET ALL (Administration department only)
    // ============================================================

    @GetMapping
    public ResponseEntity<List<TransitionResponse>> getAll(
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                transitionService.getAll()
        );
    }


    // ============================================================
    // GET BY QUEUE ACTION (Administration department only)
    // ============================================================

    @GetMapping("/queue-action/{queueActionId}")
    public ResponseEntity<List<TransitionResponse>>
    getByQueueAction(
            @PathVariable Long queueActionId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                transitionService
                        .getByQueueAction(
                                queueActionId
                        )
        );
    }


    // ============================================================
    // UPDATE (Administration department only)
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<TransitionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TransitionRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                transitionService.update(
                        id,
                        request
                )
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

        transitionService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
