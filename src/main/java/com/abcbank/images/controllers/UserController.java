package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.user.UserResponse;
import com.abcbank.images.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(
                userService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                userService.getUserResponseById(id)
        );
    }
}
