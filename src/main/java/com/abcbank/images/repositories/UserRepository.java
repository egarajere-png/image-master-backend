package com.abcbank.images.repositories;

import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Standard Spring Data repository for User.java — name/uniqueness lookups plus the join-table queries 
 * (by-queue, by-user, by-action) needed for admin member-management and workflow checks.
 *  No custom logic beyond derived query methods. */

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakId(String keycloakId);

    boolean existsByKeycloakId(String keycloakId);

    List<User> findByDepartment(Department department);

    List<User> findByDepartmentIsNull();
}