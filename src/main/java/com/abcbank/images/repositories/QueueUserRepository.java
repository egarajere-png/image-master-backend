package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.QueueUser;
import com.abcbank.images.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Standard Spring Data repository for QueueUser.java — name/uniqueness lookups plus the join-table queries 
 * (by-queue, by-user, by-action) needed for admin member-management and workflow checks.
 *  No custom logic beyond derived query methods. */

public interface QueueUserRepository
        extends JpaRepository<QueueUser, Long> {

    List<QueueUser> findByQueue(Queue queue);

    List<QueueUser> findByUser(User user);

    Optional<QueueUser> findByQueueAndUser(
            Queue queue,
            User user
    );

    boolean existsByQueueAndUser(
            Queue queue,
            User user
    );
}