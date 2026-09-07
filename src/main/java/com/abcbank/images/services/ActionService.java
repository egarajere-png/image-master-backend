package com.abcbank.images.services;

import com.abcbank.images.domain.dto.action.ActionRequest;
import com.abcbank.images.domain.dto.action.ActionResponse;
import com.abcbank.images.domain.entities.Action;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.mappers.ActionMapper;
import com.abcbank.images.repositories.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;
    private final ActionMapper actionMapper;

    // ============================================================
    // CREATE
    // ============================================================

    @Transactional
    public ActionResponse create(ActionRequest request) {

        if (actionRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException(
                    "Action already exists: " + request.getName()
            );
        }

        Action action = Action.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();

        return actionMapper.toResponse(
                actionRepository.save(action)
        );
    }

    // ============================================================
    // GET ENTITY
    // ============================================================

    @Transactional(readOnly = true)
    public Action getEntityById(Long id) {

        return actionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Action not found: " + id
                        )
                );
    }

    // ============================================================
    // GET
    // ============================================================

    @Transactional(readOnly = true)
    public ActionResponse getById(Long id) {

        return actionMapper.toResponse(
                getEntityById(id)
        );
    }

    // ============================================================
    // LIST
    // ============================================================

    @Transactional(readOnly = true)
    public List<ActionResponse> getAll() {

        return actionRepository.findAll()
                .stream()
                .map(actionMapper::toResponse)
                .toList();
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Transactional
    public ActionResponse update(
            Long id,
            ActionRequest request
    ) {

        Action action = getEntityById(id);

        actionRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Action already exists: "
                                        + request.getName()
                        );
                    }
                });

        action.setName(request.getName().trim());
        action.setDescription(request.getDescription());

        return actionMapper.toResponse(
                actionRepository.save(action)
        );
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Transactional
    public void delete(Long id) {

        Action action = getEntityById(id);

        actionRepository.delete(action);
    }
}