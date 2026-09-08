package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.department.DepartmentRequest;
import com.abcbank.images.domain.dto.department.DepartmentResponse;
import com.abcbank.images.domain.dto.user.UserResponse;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.mappers.UserMapper;
import com.abcbank.images.security.AdminAccessGuard;
import com.abcbank.images.services.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for departments (branches): CRUD, assign/remove user, and list users in a department — all writes admin-gated via AdminAccessGuard. 
 * GET endpoints stay open since department names/lists aren't sensitive. */

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserMapper userMapper;
    private final AdminAccessGuard adminAccessGuard;

    // ============================================================
    // CREATE (Administration department only)
    // ============================================================

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(
            @Valid @RequestBody DepartmentRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(departmentService.create(request));
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    // ============================================================
    // UPDATE (Administration department only)
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        return ResponseEntity.ok(
                departmentService.update(id, request)
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

        departmentService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // ASSIGN USER (Administration department only)
    // ============================================================

    @PutMapping("/{departmentId}/users/{userId}")
    public ResponseEntity<Void> assignUser(
            @PathVariable Long departmentId,
            @PathVariable Long userId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        departmentService.assignUser(departmentId, userId);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // REMOVE USER (Administration department only)
    // ============================================================

    @DeleteMapping("/{departmentId}/users/{userId}")
    public ResponseEntity<Void> removeUser(
            @PathVariable Long departmentId,
            @PathVariable Long userId,
            JwtAuthenticationToken authentication
    ) {
        adminAccessGuard.requireAdmin(authentication);

        departmentService.removeUser(departmentId, userId);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // GET USERS IN DEPARTMENT
    // ============================================================

    @GetMapping("/{departmentId}/users")
    public ResponseEntity<List<UserResponse>> getUsers(
            @PathVariable Long departmentId
    ) {
        List<User> users = departmentService.getUsers(departmentId);

        return ResponseEntity.ok(
                users.stream()
                        .map(userMapper::toResponse)
                        .toList()
        );
    }
}
