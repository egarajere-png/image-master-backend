package com.abcbank.images.services;

import com.abcbank.images.domain.dto.department.DepartmentRequest;
import com.abcbank.images.domain.dto.department.DepartmentResponse;
import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueUser;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.mappers.DepartmentMapper;
import com.abcbank.images.repositories.DepartmentRepository;
import com.abcbank.images.repositories.QueueRepository;
import com.abcbank.images.repositories.QueueUserRepository;
import com.abcbank.images.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final QueueRepository queueRepository;
    private final QueueUserRepository queueUserRepository;
    private final DepartmentMapper departmentMapper;

    // ============================================================
    // CREATE
    // ============================================================

    @Transactional
    public DepartmentResponse create(
            DepartmentRequest request
    ) {

        String departmentName =
                request.getName().trim();

        if (departmentRepository
                .existsByNameIgnoreCase(departmentName)) {

            throw new IllegalArgumentException(
                    "Department already exists: "
                            + departmentName
            );
        }

        Department department =
                Department.builder()
                        .name(departmentName)
                        .description(
                                normalizeDescription(
                                        request.getDescription()
                                )
                        )
                        .build();

        Department savedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(
                savedDepartment
        );
    }

    // ============================================================
    // GET ENTITY
    // ============================================================

    @Transactional(readOnly = true)
    public Department getEntityById(
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

    // ============================================================
    // GET BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public DepartmentResponse getById(
            Long id
    ) {

        return departmentMapper.toResponse(
                getEntityById(id)
        );
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {

        return departmentRepository
                .findAll()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Transactional
    public DepartmentResponse update(
            Long id,
            DepartmentRequest request
    ) {

        Department department =
                getEntityById(id);

        String departmentName =
                request.getName().trim();

        departmentRepository
                .findByNameIgnoreCase(
                        departmentName
                )
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "Department already exists: "
                                        + departmentName
                        );
                    }
                });

        department.setName(
                departmentName
        );

        department.setDescription(
                normalizeDescription(
                        request.getDescription()
                )
        );

        Department updatedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(
                updatedDepartment
        );
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Transactional
    public void delete(
            Long id
    ) {

        Department department =
                getEntityById(id);

        /*
         * Do not allow deletion of a department while users
         * are still assigned to it.
         */
        List<User> users =
                userRepository.findByDepartment(
                        department
                );

        if (!users.isEmpty()) {

            throw new IllegalStateException(
                    "Cannot delete department because "
                            + users.size()
                            + " user(s) are assigned to it"
            );
        }

        departmentRepository.delete(
                department
        );
    }

    // ============================================================
    // ASSIGN USER TO DEPARTMENT
    // ============================================================

    /**
     * Assigns a user to a department AND syncs their queue
     * membership to match: they're added to every queue in the new
     * department, and — if they were previously in a different
     * department — removed from every queue in the old one. A user
     * should never retain queue access to a branch they no longer
     * belong to, and shouldn't need to be manually re-added to each
     * of their new branch's queues one at a time.
     */
    @Transactional
    public void assignUser(
            Long departmentId,
            Long userId
    ) {

        Department department =
                getEntityById(
                        departmentId
                );

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found: "
                                                + userId
                                )
                        );

        Department previousDepartment =
                user.getDepartment();

        user.setDepartment(
                department
        );

        userRepository.save(
                user
        );

        boolean isChangingDepartment =
                previousDepartment != null
                        && !previousDepartment.getId()
                        .equals(department.getId());

        if (isChangingDepartment) {
            removeUserFromDepartmentQueues(previousDepartment, user);
        }

        addUserToDepartmentQueues(department, user);
    }

    // ============================================================
    // REMOVE USER FROM DEPARTMENT
    // ============================================================

    @Transactional
    public void removeUser(
            Long departmentId,
            Long userId
    ) {

        Department department =
                getEntityById(
                        departmentId
                );

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found: "
                                                + userId
                                )
                        );

        if (user.getDepartment() == null
                || !user.getDepartment()
                .getId()
                .equals(
                        department.getId()
                )) {

            throw new ResourceNotFoundException(
                    "User is not assigned to this department"
            );
        }

        user.setDepartment(
                null
        );

        userRepository.save(
                user
        );

        removeUserFromDepartmentQueues(department, user);
    }

    // ============================================================
    // QUEUE MEMBERSHIP SYNC HELPERS
    // ============================================================

    private void addUserToDepartmentQueues(
            Department department,
            User user
    ) {

        List<Queue> departmentQueues =
                queueRepository.findByDepartment(department);

        for (Queue queue : departmentQueues) {

            boolean alreadyMember =
                    queueUserRepository.existsByQueueAndUser(
                            queue,
                            user
                    );

            if (!alreadyMember) {

                queueUserRepository.save(
                        QueueUser.builder()
                                .queue(queue)
                                .user(user)
                                .build()
                );
            }
        }
    }

    private void removeUserFromDepartmentQueues(
            Department department,
            User user
    ) {

        List<Queue> departmentQueues =
                queueRepository.findByDepartment(department);

        for (Queue queue : departmentQueues) {

            queueUserRepository
                    .findByQueueAndUser(queue, user)
                    .ifPresent(queueUserRepository::delete);
        }
    }

    // ============================================================
    // GET USERS IN DEPARTMENT
    // ============================================================

    @Transactional(readOnly = true)
    public List<User> getUsers(
            Long departmentId
    ) {

        Department department =
                getEntityById(
                        departmentId
                );

        return userRepository.findByDepartment(
                department
        );
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    private String normalizeDescription(
            String description
    ) {

        if (description == null) {
            return null;
        }

        String value =
                description.trim();

        return value.isBlank()
                ? null
                : value;
    }
}