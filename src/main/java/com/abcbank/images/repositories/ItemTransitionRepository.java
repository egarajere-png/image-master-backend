package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Item;
import com.abcbank.images.domain.entities.ItemTransition;
import com.abcbank.images.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemTransitionRepository
        extends JpaRepository<ItemTransition, Long> {

    List<ItemTransition> findByItemOrderByCreatedAtAsc(Item item);

    List<ItemTransition> findByItemOrderByCreatedAtDesc(Item item);

    List<ItemTransition> findByPerformedBy(User user);
}