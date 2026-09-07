package com.abcbank.images.services;

import com.abcbank.images.domain.dto.queue.QueueActionResponse;
import com.abcbank.images.domain.dto.queue.QueueRequest;
import com.abcbank.images.domain.dto.queue.QueueResponse;
import com.abcbank.images.domain.dto.user.UserResponse;
import com.abcbank.images.domain.entities.Action;
import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueAction;
import com.abcbank.images.domain.entities.QueueUser;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.domain.enums.QueueStatus;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.mappers.QueueActionMapper;
import com.abcbank.images.mappers.QueueMapper;
import com.abcbank.images.mappers.UserMapper;
import com.abcbank.images.repositories.ActionRepository;
import com.abcbank.images.repositories.DepartmentRepository;
import com.abcbank.images.repositories.QueueActionRepository;
import com.abcbank.images.repositories.QueueRepository;
import com.abcbank.images.repositories.QueueUserRepository;
import com.abcbank.images.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final QueueUserRepository queueUserRepository;
    private final QueueActionRepository queueActionRepository;
    private final UserRepository userRepository;
    private final ActionRepository actionRepository;
    private final DepartmentRepository departmentRepository;

    private final QueueMapper queueMapper;
    private final QueueActionMapper queueActionMapper;
    private final UserMapper userMapper;


    // ============================================================
    // CREATE
    // ============================================================

    @Transactional
    public QueueResponse create(QueueRequest request) {

        if (queueRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException(
                    "Queue already exists: " + request.getName()
            );
        }

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found: " + request.getDepartmentId()
                        )
                );

        Queue queue = Queue.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .status(QueueStatus.ACTIVE)
                .initial(false)
                .department(department)
                .build();

        Queue savedQueue = queueRepository.save(queue);

        ensureSingleInitialQueue(
                savedQueue,
                request.isInitial()
        );

        return queueMapper.toResponse(
                queueRepository.save(savedQueue)
        );
    }


    // ============================================================
    // GET
    // ============================================================

    @Transactional(readOnly = true)
    public Queue getEntityById(Long id) {

        return queueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Queue not found: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public QueueResponse getById(Long id) {

        return queueMapper.toResponse(
                getEntityById(id)
        );
    }


    // ============================================================
    // LIST
    // ============================================================

    @Transactional(readOnly = true)
    public List<QueueResponse> getAll() {

        return queueRepository.findAll()
                .stream()
                .map(queueMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QueueResponse> getActiveQueues() {

        return queueRepository.findByStatus(QueueStatus.ACTIVE)
                .stream()
                .map(queueMapper::toResponse)
                .toList();
    }

    /**
     * Get active queues accessible to the user.
     * 
     * - Admins (is_admin=true) see all active queues
     * - Other users see active queues from their department only
     */
    @Transactional(readOnly = true)
    public List<QueueResponse> getAccessibleQueues(User user) {
        List<Queue> queues;

        if (user.getIsAdmin()) {
            // Admins see all active queues
            queues = queueRepository.findByStatus(QueueStatus.ACTIVE);
        } else {
            // Non-admins see only queues from their department
            queues = queueRepository.findByStatusAndDepartment(
                    QueueStatus.ACTIVE,
                    user.getDepartment()
            );
        }

        return queues.stream()
                .map(queueMapper::toResponse)
                .toList();
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @Transactional
    public QueueResponse update(
            Long id,
            QueueRequest request
    ) {

        Queue queue = getEntityById(id);

        queueRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Queue already exists: "
                                        + request.getName()
                        );
                    }
                });

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found: " + request.getDepartmentId()
                        )
                );

        queue.setName(request.getName().trim());
        queue.setDescription(request.getDescription());
        queue.setDepartment(department);

        ensureSingleInitialQueue(
                queue,
                request.isInitial()
        );

        return queueMapper.toResponse(
                queueRepository.save(queue)
        );
    }


    // ============================================================
    // ACTIVATE
    // ============================================================

    @Transactional
    public QueueResponse activate(Long id) {

        Queue queue = getEntityById(id);

        queue.setStatus(QueueStatus.ACTIVE);

        return queueMapper.toResponse(
                queueRepository.save(queue)
        );
    }


    // ============================================================
    // DEACTIVATE
    // ============================================================

    @Transactional
    public QueueResponse deactivate(Long id) {

        Queue queue = getEntityById(id);

        /*
         * The initial queue is required when creating new items.
         * Therefore, an initial queue cannot be deactivated.
         */
        if (queue.isInitial()) {
            throw new IllegalStateException(
                    "Cannot deactivate the initial workflow queue. "
                            + "Assign another initial queue first."
            );
        }

        queue.setStatus(QueueStatus.INACTIVE);

        return queueMapper.toResponse(
                queueRepository.save(queue)
        );
    }


    // ============================================================
    // ASSIGN USER
    // ============================================================

    @Transactional
    public void assignUser(
            Long queueId,
            Long userId
    ) {

        Queue queue = getEntityById(queueId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + userId
                        )
                );

        if (queueUserRepository.existsByQueueAndUser(queue, user)) {
            return;
        }

        QueueUser queueUser = QueueUser.builder()
                .queue(queue)
                .user(user)
                .build();

        queueUserRepository.save(queueUser);
    }


    // ============================================================
    // REMOVE USER
    // ============================================================

    @Transactional
    public void removeUser(
            Long queueId,
            Long userId
    ) {

        Queue queue = getEntityById(queueId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + userId
                        )
                );

        QueueUser queueUser =
                queueUserRepository.findByQueueAndUser(
                        queue,
                        user
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User is not assigned to queue"
                        )
                );

        queueUserRepository.delete(queueUser);
    }


    // ============================================================
    // GET USERS IN QUEUE
    // ============================================================

    /**
     * Convert QueueUser -> UserResponse while the Hibernate
     * transaction is still active.
     *
     * QueueUser.user is LAZY, so returning User entities to the
     * controller would cause LazyInitializationException when
     * MapStruct accesses them after the transaction closes.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(Long queueId) {

        Queue queue = getEntityById(queueId);

        return queueUserRepository.findByQueue(queue)
                .stream()
                .map(QueueUser::getUser)
                .map(userMapper::toResponse)
                .toList();
    }


    // ============================================================
    // ASSIGN ACTION
    // ============================================================

    @Transactional
    public QueueActionResponse assignAction(
            Long queueId,
            Long actionId
    ) {

        Queue queue = getEntityById(queueId);

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Action not found: " + actionId
                        )
                );

        if (queueActionRepository.existsByQueueAndAction(
                queue,
                action
        )) {

            QueueAction existing =
                    queueActionRepository
                            .findByQueueAndAction(queue, action)
                            .orElseThrow();

            return queueActionMapper.toResponse(existing);
        }

        QueueAction queueAction = QueueAction.builder()
                .queue(queue)
                .action(action)
                .build();

        return queueActionMapper.toResponse(
                queueActionRepository.save(queueAction)
        );
    }


    // ============================================================
    // REMOVE ACTION
    // ============================================================

    @Transactional
    public void removeAction(
            Long queueId,
            Long actionId
    ) {

        Queue queue = getEntityById(queueId);

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Action not found: " + actionId
                        )
                );

        QueueAction queueAction =
                queueActionRepository.findByQueueAndAction(
                        queue,
                        action
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Action is not assigned to queue"
                        )
                );

        queueActionRepository.delete(queueAction);
    }


    // ============================================================
    // GET QUEUE ACTIONS
    // ============================================================

    @Transactional(readOnly = true)
    public List<QueueActionResponse> getActions(Long queueId) {

        Queue queue = getEntityById(queueId);

        return queueActionRepository.findByQueue(queue)
                .stream()
                .map(queueActionMapper::toResponse)
                .toList();
    }


    // ============================================================
    // INITIAL QUEUE MANAGEMENT
    // ============================================================

    /**
     * Ensures that only one queue per department is configured as
     * that department's initial workflow queue.
     *
     * Each department runs its own isolated workflow, so each one
     * needs its own entry point — this used to be a single global
     * initial queue, which meant every branch had to share it.
     *
     * When a new queue is marked as initial, any existing initial
     * queue *in the same department* is automatically unset.
     */
    private void ensureSingleInitialQueue(
            Queue queue,
            boolean shouldBeInitial
    ) {

        if (!shouldBeInitial) {

            /*
             * If this queue was previously the initial queue,
             * allow it to be unset.
             */
            queue.setInitial(false);
            return;
        }

        queueRepository.findByInitialTrueAndDepartment(queue.getDepartment())
                .filter(existing ->
                        existing.getId() != null
                                && !existing.getId()
                                .equals(queue.getId())
                )
                .ifPresent(existing -> {

                    existing.setInitial(false);

                    queueRepository.save(existing);
                });

        queue.setInitial(true);
    }
}