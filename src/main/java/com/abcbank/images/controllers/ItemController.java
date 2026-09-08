package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.item.ItemActionRequest;
import com.abcbank.images.domain.dto.item.ItemRequest;
import com.abcbank.images.domain.dto.item.ItemResponse;
import com.abcbank.images.domain.dto.transition.ItemTransitionResponse;
import com.abcbank.images.domain.enums.ItemStatus;
import com.abcbank.images.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST endpoints for items: create, read (by id/all/active/queue/
 * status/creator), execute a workflow action, and fetch history.
 *
 * create() and executeAction() now compute and pass adminRole
 * (from the ROLE_ADMIN JWT authority) into the service layer,
 * matching the pattern already used elsewhere — WorkflowService
 * needs it to know whether to bypass the normal queue-membership
 * check for an admin acting on someone else's queue.
 *
 * amend() is new: POST /{itemId}/amend, multipart, lets a Teller
 * replace an item's photo and/or correct its identity fields in the
 * same request that executes the AMEND action — this is the
 * endpoint the frontend's ActionPanel was previously missing.
 */


@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> create(
            @Valid @RequestBody ItemRequest request,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.create(
                                request,
                                authentication.getName(),
                                authentication instanceof JwtAuthenticationToken jwt
                                        && jwt.getAuthorities().stream()
                                        .anyMatch(authority ->
                                                authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                itemService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll() {
        return ResponseEntity.ok(
                itemService.getAll()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<ItemResponse>> getActive() {
        return ResponseEntity.ok(
                itemService.getActiveItems()
        );
    }

    @GetMapping("/queue/{queueId}")
    public ResponseEntity<List<ItemResponse>> getByQueue(
            @PathVariable Long queueId
    ) {
        return ResponseEntity.ok(
                itemService.getByQueue(queueId)
        );
    }

    @GetMapping("/queue/{queueId}/active")
    public ResponseEntity<List<ItemResponse>> getActiveByQueue(
            @PathVariable Long queueId
    ) {
        return ResponseEntity.ok(
                itemService.getActiveByQueue(queueId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ItemResponse>> getByStatus(
            @PathVariable ItemStatus status
    ) {
        return ResponseEntity.ok(
                itemService.getByStatus(status)
        );
    }

    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<ItemResponse>> getCreatedByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                itemService.getCreatedByUser(userId)
        );
    }

    @PostMapping("/{itemId}/actions")
    public ResponseEntity<ItemResponse> executeAction(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemActionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                itemService.executeAction(
                        itemId,
                        request,
                                authentication.getName(),
                                authentication instanceof JwtAuthenticationToken jwt
                                        && jwt.getAuthorities().stream()
                                        .anyMatch(authority ->
                                                authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))
                )
        );
    }

    /**
     * Amend an item: optionally replace its photo and/or its
     * customer identity fields, then execute the "AMEND" workflow
     * action. This is the endpoint a Teller uses when an item is
     * returned to them — the photo can be swapped for a new one
     * before it's sent back into the workflow.
     */
    @PostMapping(
            value = "/{itemId}/amend",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ItemResponse> amend(
            @PathVariable Long itemId,

            @RequestParam(value = "file", required = false)
            MultipartFile file,

            @RequestParam(value = "idNumber", required = false)
            String idNumber,

            @RequestParam(value = "customerName", required = false)
            String customerName,

            @RequestParam(value = "phoneNumber", required = false)
            String phoneNumber,

            @RequestParam(value = "description", required = false)
            String description,

            @RequestParam(value = "comment", required = false)
            String comment,

            Authentication authentication
    ) {
        return ResponseEntity.ok(
                itemService.amendWithImage(
                        itemId,
                        file,
                        idNumber,
                        customerName,
                        phoneNumber,
                        description,
                        comment,
                        authentication.getName(),
                        authentication instanceof JwtAuthenticationToken jwt
                                && jwt.getAuthorities().stream()
                                .anyMatch(authority ->
                                        authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))
                )
        );
    }

    @GetMapping("/{itemId}/history")
    public ResponseEntity<List<ItemTransitionResponse>> getHistory(
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(
                itemService.getHistory(itemId)
        );
    }
}