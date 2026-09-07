package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, Long> {

    Optional<Action> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}