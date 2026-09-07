package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueAction;
import com.abcbank.images.domain.entities.Transition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransitionRepository
        extends JpaRepository<Transition, Long> {

    // ============================================================
    // FIND BY QUEUE ACTION
    // ============================================================

    List<Transition> findByQueueAction(
            QueueAction queueAction
    );

    // ============================================================
    // FIND BY QUEUE ACTION AND DEPARTMENT
    // ============================================================

    Optional<Transition> findByQueueActionAndDepartment(
            QueueAction queueAction,
            Department department
    );

    // ============================================================
    // FIND BY DESTINATION QUEUE
    // ============================================================

    List<Transition> findByDestinationQueue(
            Queue destinationQueue
    );

    // ============================================================
    // FIND BY DEPARTMENT
    // ============================================================

    List<Transition> findByDepartment(
            Department department
    );
}