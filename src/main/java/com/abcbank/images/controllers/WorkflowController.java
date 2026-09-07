package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.transition.ItemTransitionResponse;
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

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final TransitionService transitionService;
    private final AdminAccessGuard adminAccessGuard;

    // ============================================================
    // CREATE TRANSITION (Administration department only)
    // ============================================================

    @PostMapping("/transitions")
    public ResponseEntity<TransitionResponse> createTransition(
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
    // GET ALL TRANSITIONS
    // ============================================================

    @GetMapping("/transitions")
    public ResponseEntity<List<TransitionResponse>> getAllTransitions() {

        return ResponseEntity.ok(
                transitionService.getAll()
        );
    }

    // ============================================================
    // GET TRANSITION
    // ============================================================

    @GetMapping("/transitions/{id}")
    public ResponseEntity<TransitionResponse> getTransition(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                transitionService.getById(id)
        );
    }

    // ============================================================
    // UPDATE TRANSITION (Administration department only)
    // ============================================================

    @PutMapping("/transitions/{id}")
    public ResponseEntity<TransitionResponse> updateTransition(
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
    // DELETE TRANSITION (Administration department only)
    // ============================================================

    @DeleteMapping("/transitions/{id}")
    public ResponseEntity<Void> deleteTransition(
            @PathVariable Long id,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        transitionService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ============================================================
    // GET TRANSITIONS FOR QUEUE — stays open: ActionPanel depends
    // on this for every authenticated user to know which actions
    // are actually executable for their department.
    // ============================================================

    @GetMapping("/queues/{queueId}/transitions")
    public ResponseEntity<List<TransitionResponse>> getQueueTransitions(
            @PathVariable Long queueId
    ) {

        return ResponseEntity.ok(
                transitionService.getForQueue(queueId)
        );
    }

    // ============================================================
    // GET TRANSITIONS FOR QUEUE ACTION
    // ============================================================

    @GetMapping(
            "/queue-actions/{queueActionId}/transitions"
    )
    public ResponseEntity<List<TransitionResponse>>
    getQueueActionTransitions(
            @PathVariable Long queueActionId
    ) {

        return ResponseEntity.ok(
                transitionService.getForQueueAction(
                        queueActionId
                )
        );
    }

    // ============================================================
    // GET TRANSITIONS FOR DEPARTMENT
    // ============================================================

    @GetMapping(
            "/departments/{departmentId}/transitions"
    )
    public ResponseEntity<List<TransitionResponse>>
    getDepartmentTransitions(
            @PathVariable Long departmentId
    ) {

        return ResponseEntity.ok(
                transitionService.getForDepartment(
                        departmentId
                )
        );
    }

    // ============================================================
    // GET ITEM HISTORY — stays open: every item detail page depends
    // on this for every authenticated user.
    // ============================================================

    @GetMapping("/items/{itemId}/history")
    public ResponseEntity<List<ItemTransitionResponse>>
    getItemHistory(
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                transitionService.getItemHistory(itemId)
        );
    }

    // ============================================================
    // GET ITEM HISTORY - LATEST FIRST
    // ============================================================

    @GetMapping("/items/{itemId}/history/latest")
    public ResponseEntity<ItemTransitionResponse>
    getLatestItemHistory(
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                transitionService.getLatestItemHistory(itemId)
        );
    }
}
