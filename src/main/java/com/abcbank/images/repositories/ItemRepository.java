package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Item;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Item queries by current queue, status, and creator. findByCurrentQueue/AndStatus back the per-queue item lists 
 * (My Queue, Admin → All Items); no department filter needed here since a queue's department already scopes it. */

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCurrentQueue(Queue queue);

    List<Item> findByCurrentQueueAndStatus(
            Queue queue,
            ItemStatus status
    );

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByCreatedById(Long userId);
}