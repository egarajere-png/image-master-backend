package com.abcbank.images.services;

import com.abcbank.images.domain.dto.transition.ItemTransitionResponse;
import com.abcbank.images.domain.dto.transition.TransitionRequest;
import com.abcbank.images.domain.dto.transition.TransitionResponse;
import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Item;
import com.abcbank.images.domain.entities.ItemTransition;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueAction;
import com.abcbank.images.domain.entities.Transition;
import com.abcbank.images.domain.enums.WorkflowOutcome;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.mappers.ItemTransitionMapper;
import com.abcbank.images.mappers.TransitionMapper;
import com.abcbank.images.repositories.DepartmentRepository;
import com.abcbank.images.repositories.ItemRepository;
import com.abcbank.images.repositories.ItemTransitionRepository;
import com.abcbank.images.repositories.QueueActionRepository;
import com.abcbank.images.repositories.QueueRepository;
import com.abcbank.images.repositories.TransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Admin-facing CRUD for Transition rows (the routing table), plus
 * the read paths used to answer "what actions can this queue/queue-
 * action/department do" and to serve item history
 * (getItemHistory/getLatestItemHistory — backs the workflow timeline
 * and QueuePositionTrack). validateTransitionConfiguration enforces
 * the "destination queue XOR outcome, never both, never neither"
 * rule on every create/update.
 */

@Service
@RequiredArgsConstructor
public class TransitionService {

    private final TransitionRepository transitionRepository;
    private final QueueActionRepository queueActionRepository;
    private final DepartmentRepository departmentRepository;
    private final QueueRepository queueRepository;
    private final ItemRepository itemRepository;
    private final ItemTransitionRepository itemTransitionRepository;

    private final TransitionMapper transitionMapper;
    private final ItemTransitionMapper itemTransitionMapper;


    // ============================================================
    // CREATE
    // ============================================================

    @Transactional
    public TransitionResponse create(
            TransitionRequest request
    ) {

        QueueAction queueAction =
                getQueueAction(request.getQueueActionId());

        Department department =
                getDepartment(request.getDepartmentId());

        Queue destinationQueue =
                resolveDestinationQueue(
                        request.getDestinationQueueId()
                );

        validateTransitionConfiguration(
                request.getDestinationQueueId(),
                request.getOutcome()
        );

        if (transitionRepository
                .findByQueueActionAndDepartment(
                        queueAction,
                        department
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "A transition already exists for this queue action "
                            + "and department"
            );
        }

        Transition transition =
                Transition.builder()
                        .queueAction(queueAction)
                        .department(department)
                        .destinationQueue(destinationQueue)
                        .outcome(request.getOutcome())
                        .description(request.getDescription())
                        .build();

        return transitionMapper.toResponse(
                transitionRepository.save(transition)
        );
    }


    // ============================================================
    // GET ENTITY
    // ============================================================

    @Transactional(readOnly = true)
    public Transition getEntityById(
            Long id
    ) {

        return transitionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transition not found: " + id
                        )
                );
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public TransitionResponse getById(
            Long id
    ) {

        return transitionMapper.toResponse(
                getEntityById(id)
        );
    }


    // ============================================================
    // GET ALL
    // ============================================================

    @Transactional(readOnly = true)
    public List<TransitionResponse> getAll() {

        return transitionRepository
                .findAll()
                .stream()
                .map(transitionMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET BY QUEUE ACTION
    // ============================================================

    @Transactional(readOnly = true)
    public List<TransitionResponse> getByQueueAction(
            Long queueActionId
    ) {

        QueueAction queueAction =
                getQueueAction(queueActionId);

        return transitionRepository
                .findByQueueAction(queueAction)
                .stream()
                .map(transitionMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET FOR QUEUE
    // ============================================================

    @Transactional(readOnly = true)
    public List<TransitionResponse> getForQueue(
            Long queueId
    ) {

        Queue queue =
                queueRepository
                        .findById(queueId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Queue not found: " + queueId
                                )
                        );

        return transitionRepository
                .findAll()
                .stream()
                .filter(
                        transition ->
                                transition.getQueueAction()
                                        .getQueue()
                                        .getId()
                                        .equals(queue.getId())
                )
                .map(transitionMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET FOR QUEUE ACTION
    // ============================================================

    @Transactional(readOnly = true)
    public List<TransitionResponse> getForQueueAction(
            Long queueActionId
    ) {

        return getByQueueAction(
                queueActionId
        );
    }


    // ============================================================
    // GET FOR DEPARTMENT
    // ============================================================

    @Transactional(readOnly = true)
    public List<TransitionResponse> getForDepartment(
            Long departmentId
    ) {

        Department department =
                getDepartment(departmentId);

        return transitionRepository
                .findByDepartment(department)
                .stream()
                .map(transitionMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET ITEM HISTORY
    // ============================================================

    @Transactional(readOnly = true)
    public List<ItemTransitionResponse> getItemHistory(
            Long itemId
    ) {

        Item item =
                itemRepository
                        .findById(itemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Item not found: " + itemId
                                )
                        );

        List<ItemTransition> itemTransitions =
                itemTransitionRepository
                        .findByItemOrderByCreatedAtAsc(
                                item
                        );

        return itemTransitions
                .stream()
                .map(
                        itemTransitionMapper::toResponse
                )
                .toList();
    }


    // ============================================================
    // GET LATEST ITEM HISTORY
    // ============================================================

    @Transactional(readOnly = true)
    public ItemTransitionResponse getLatestItemHistory(
            Long itemId
    ) {

        Item item =
                itemRepository
                        .findById(itemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Item not found: " + itemId
                                )
                        );

        return itemTransitionRepository
                .findByItemOrderByCreatedAtDesc(
                        item
                )
                .stream()
                .findFirst()
                .map(
                        itemTransitionMapper::toResponse
                )
                .orElse(null);
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @Transactional
    public TransitionResponse update(
            Long id,
            TransitionRequest request
    ) {

        Transition transition =
                getEntityById(id);

        QueueAction queueAction =
                getQueueAction(request.getQueueActionId());

        Department department =
                getDepartment(request.getDepartmentId());

        Queue destinationQueue =
                resolveDestinationQueue(
                        request.getDestinationQueueId()
                );

        validateTransitionConfiguration(
                request.getDestinationQueueId(),
                request.getOutcome()
        );

        transitionRepository
                .findByQueueActionAndDepartment(
                        queueAction,
                        department
                )
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "A transition already exists for this "
                                        + "queue action and department"
                        );
                    }
                });

        transition.setQueueAction(queueAction);
        transition.setDepartment(department);
        transition.setDestinationQueue(destinationQueue);
        transition.setOutcome(request.getOutcome());
        transition.setDescription(
                request.getDescription()
        );

        return transitionMapper.toResponse(
                transitionRepository.save(transition)
        );
    }


    // ============================================================
    // DELETE
    // ============================================================

    @Transactional
    public void delete(
            Long id
    ) {

        Transition transition =
                getEntityById(id);

        transitionRepository.delete(
                transition
        );
    }


    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    private QueueAction getQueueAction(
            Long id
    ) {

        return queueActionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Queue action not found: " + id
                        )
                );
    }


    private Department getDepartment(
            Long id
    ) {

        return departmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found: " + id
                        )
                );
    }


    private Queue resolveDestinationQueue(
            Long destinationQueueId
    ) {

        if (destinationQueueId == null) {
            return null;
        }

        return queueRepository
                .findById(destinationQueueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination queue not found: "
                                        + destinationQueueId
                        )
                );
    }


    // ============================================================
    // VALIDATE TRANSITION
    // ============================================================

    /**
     * A transition must do exactly one of the following:
     *
     * 1. Move the item to another queue
     *
     * OR
     *
     * 2. End the workflow with an outcome.
     */
    private void validateTransitionConfiguration(
            Long destinationQueueId,
            WorkflowOutcome outcome
    ) {

        boolean hasDestinationQueue =
                destinationQueueId != null;

        boolean hasOutcome =
                outcome != null;

        if (hasDestinationQueue == hasOutcome) {

            throw new IllegalArgumentException(
                    "Transition must have either a destination queue "
                            + "or a workflow outcome, but not both"
            );
        }
    }
}