package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Standard Spring Data repository for Action.java — name/uniqueness lookups plus the join-table queries 
 * (by-queue, by-user, by-action) needed for admin member-management and workflow checks.
 *  No custom logic beyond derived query methods. */

public interface ActionRepository extends JpaRepository<Action, Long> {

    Optional<Action> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}