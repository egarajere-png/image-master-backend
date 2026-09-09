package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    Optional<Queue> findByNameIgnoreCase(String name);

    Optional<Queue> findByInitialTrue();

    Optional<Queue> findByInitialTrueAndDepartment(Department department);

    List<Queue> findByStatus(QueueStatus status);

    List<Queue> findByStatusAndDepartment(QueueStatus status, Department department);

    /**
     * Every queue in a department, regardless of ACTIVE/INACTIVE
     * status — used when syncing a user's queue membership to their
     * department, since membership shouldn't depend on a queue's
     * current activation state.
     */
    List<Queue> findByDepartment(Department department);

    boolean existsByNameIgnoreCase(String name);
}