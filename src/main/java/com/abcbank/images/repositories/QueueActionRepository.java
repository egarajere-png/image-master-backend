package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Action;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Standard Spring Data repository for QueueAction.java — name/uniqueness lookups plus the join-table queries 
 * (by-queue, by-user, by-action) needed for admin member-management and workflow checks.
 *  No custom logic beyond derived query methods. */

public interface QueueActionRepository extends JpaRepository<QueueAction, Long> {

    List<QueueAction> findByQueue(Queue queue);

    List<QueueAction> findByAction(Action action);

    Optional<QueueAction> findByQueueAndAction(
            Queue queue,
            Action action
    );

    boolean existsByQueueAndAction(
            Queue queue,
            Action action
    );
}