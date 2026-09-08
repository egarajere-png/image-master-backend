package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * findByInitialTrueAndDepartment is the department-scoped
 * replacement for the old findByInitialTrue — each department now
 * resolves its own initial queue independently (used by
 * ItemService.create() to find where a new item should start).
 * findByStatusAndDepartment backs QueueService.getAccessibleQueues,
 * which is what actually filters "My Queue" per branch.
 */


public interface QueueRepository extends JpaRepository<Queue, Long> {

    Optional<Queue> findByNameIgnoreCase(String name);

    Optional<Queue> findByInitialTrue();

    Optional<Queue> findByInitialTrueAndDepartment(Department department);

    List<Queue> findByStatus(QueueStatus status);

    List<Queue> findByStatusAndDepartment(QueueStatus status, Department department);

    boolean existsByNameIgnoreCase(String name);
}