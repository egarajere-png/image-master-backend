package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Standard Spring Data repository for Department.java — name/uniqueness lookups plus the join-table queries 
 * (by-queue, by-user, by-action) needed for admin member-management and workflow checks.
 *  No custom logic beyond derived query methods. */

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    Optional<Department> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}