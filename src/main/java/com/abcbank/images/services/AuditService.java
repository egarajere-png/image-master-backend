package com.abcbank.images.services;

import com.abcbank.images.domain.dto.transition.ItemTransitionResponse;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.mappers.ItemTransitionMapper;
import com.abcbank.images.repositories.ItemTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Returns all workflow activity performed by a given user (their full ItemTransition history) — a thin wrapper over ItemTransitionRepository.findByPerformedBy. 
 * Not currently called from any controller; kept for a future "my activity" view. */

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ItemTransitionRepository itemTransitionRepository;
    private final ItemTransitionMapper itemTransitionMapper;

    /**
     * Returns all workflow activity performed by a user.
     */
    @Transactional(readOnly = true)
    public List<ItemTransitionResponse> getUserActivity(User user) {

        return itemTransitionRepository
                .findByPerformedBy(user)
                .stream()
                .map(itemTransitionMapper::toResponse)
                .toList();
    }
}
