package com.abcbank.images.services;

import com.abcbank.images.domain.entities.Action;
import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Item;
import com.abcbank.images.domain.entities.ItemTransition;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueAction;
import com.abcbank.images.domain.entities.Transition;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.domain.enums.ItemStatus;
import com.abcbank.images.domain.enums.WorkflowOutcome;
import com.abcbank.images.exceptions.InvalidWorkflowActionException;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.exceptions.UnauthorizedActionException;
import com.abcbank.images.repositories.ActionRepository;
import com.abcbank.images.repositories.ItemRepository;
import com.abcbank.images.repositories.ItemTransitionRepository;
import com.abcbank.images.repositories.QueueActionRepository;
import com.abcbank.images.repositories.QueueUserRepository;
import com.abcbank.images.repositories.TransitionRepository;
import com.abcbank.images.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ActionRepository actionRepository;
    private final QueueUserRepository queueUserRepository;
    private final QueueActionRepository queueActionRepository;
    private final TransitionRepository transitionRepository;
    private final ItemTransitionRepository itemTransitionRepository;

    @Transactional
    public Item executeAction(
            Long itemId,
            String actionName,
            String keycloakUserId,
            String comment,
            boolean adminRole
    ) {

        // ========================================================
        // 1. LOAD ITEM
        // ========================================================

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Item not found: " + itemId
                        )
                );

        // ========================================================
        // 2. VALIDATE ITEM STATUS
        // ========================================================

        if (item.getStatus() != ItemStatus.ACTIVE) {

            throw new InvalidWorkflowActionException(
                    "Item is no longer active: " + itemId
            );
        }

        // ========================================================
        // 3. RESOLVE CURRENT USER
        // ========================================================

        User user = userRepository
                .findByKeycloakId(keycloakUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found for Keycloak ID: "
                                        + keycloakUserId
                        )
                );

        // ========================================================
        // 4. VALIDATE USER DEPARTMENT
        // ========================================================

        Department userDepartment =
                user.getDepartment();

        if (userDepartment == null) {

            throw new UnauthorizedActionException(
                    "User is not assigned to a department"
            );
        }

        boolean administrator = adminRole
                || userDepartment.getName() != null
                && userDepartment.getName().trim().equalsIgnoreCase(
                        UserService.ADMINISTRATION_DEPARTMENT_NAME
                );

        // ========================================================
        // 5. RESOLVE CURRENT QUEUE
        // ========================================================

        Queue currentQueue =
                item.getCurrentQueue();

        if (currentQueue == null) {

            throw new InvalidWorkflowActionException(
                    "Item has no current queue: "
                            + itemId
            );
        }

        // ========================================================
        // 6. VALIDATE QUEUE STATUS
        // ========================================================

        if (currentQueue.getStatus()
                != com.abcbank.images.domain.enums.QueueStatus.ACTIVE) {

            throw new InvalidWorkflowActionException(
                    "Current queue is inactive: "
                            + currentQueue.getName()
            );
        }

        // ========================================================
        // 7. VERIFY USER BELONGS TO QUEUE
        // ========================================================

        boolean assignedToQueue =
                queueUserRepository
                        .existsByQueueAndUser(
                                currentQueue,
                                user
                        );

        if (!assignedToQueue && !administrator) {

            throw new UnauthorizedActionException(
                    "User is not assigned to queue: "
                            + currentQueue.getName()
            );
        }

        // ========================================================
        // 8. RESOLVE ACTION
        // ========================================================

        Action action =
                actionRepository
                        .findByNameIgnoreCase(
                                actionName
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Action not found: "
                                                + actionName
                                )
                        );

        // ========================================================
        // 9. VERIFY ACTION BELONGS TO QUEUE
        // ========================================================

        QueueAction queueAction =
                queueActionRepository
                        .findByQueueAndAction(
                                currentQueue,
                                action
                        )
                        .orElseThrow(() ->
                                new UnauthorizedActionException(
                                        "Action '"
                                                + action.getName()
                                                + "' is not allowed in queue '"
                                                + currentQueue.getName()
                                                + "'"
                                )
                        );

        // ========================================================
        // 10. RESOLVE DEPARTMENT-SPECIFIC TRANSITION
        // ========================================================

        Transition transition =
                transitionRepository
                        .findByQueueActionAndDepartment(
                                queueAction,
                                userDepartment
                        )
                        .orElseThrow(() ->
                                new UnauthorizedActionException(
                                        "No transition configured for action '"
                                                + action.getName()
                                                + "' in queue '"
                                                + currentQueue.getName()
                                                + "' for department '"
                                                + userDepartment.getName()
                                                + "'"
                                )
                        );

        // ========================================================
        // 11. RECORD WORKFLOW HISTORY
        // ========================================================

        ItemTransition itemTransition =
                ItemTransition.builder()
                        .item(item)
                        .transition(transition)
                        .performedBy(user)
                        .sourceQueue(currentQueue)
                        .destinationQueue(
                                transition.getDestinationQueue()
                        )
                        .actionName(action.getName())
                        .comment(comment)
                        .build();

        itemTransitionRepository.save(
                itemTransition
        );

        // ========================================================
        // 12. APPLY TRANSITION
        // ========================================================

        applyTransition(
                item,
                transition
        );

        // ========================================================
        // 13. PERSIST ITEM
        // ========================================================

        return itemRepository.save(item);
    }

    // ============================================================
    // APPLY TRANSITION
    // ============================================================

    private void applyTransition(
            Item item,
            Transition transition
    ) {

        // ========================================================
        // MOVE TO ANOTHER QUEUE
        // ========================================================

        if (transition.getDestinationQueue()
                != null) {

            Queue destinationQueue =
                    transition.getDestinationQueue();

            if (destinationQueue.getStatus()
                    != com.abcbank.images.domain.enums.QueueStatus.ACTIVE) {

                throw new InvalidWorkflowActionException(
                        "Destination queue is inactive: "
                                + destinationQueue.getName()
                );
            }

            item.setCurrentQueue(
                    destinationQueue
            );

            item.setStatus(
                    ItemStatus.ACTIVE
            );

            return;
        }

        // ========================================================
        // TERMINAL OUTCOME
        // ========================================================

        WorkflowOutcome outcome =
                transition.getOutcome();

        if (outcome == null) {

            throw new InvalidWorkflowActionException(
                    "Transition has neither destination queue nor outcome"
            );
        }

        switch (outcome) {

            case COMPLETED -> {

                item.setCurrentQueue(null);

                item.setStatus(
                        ItemStatus.COMPLETED
                );
            }

            case REMOVED -> {

                item.setCurrentQueue(null);

                item.setStatus(
                        ItemStatus.REMOVED
                );
            }

            default -> throw new InvalidWorkflowActionException(
                    "Unsupported workflow outcome: "
                            + outcome
            );
        }
    }
}