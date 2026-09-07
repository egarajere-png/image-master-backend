-- ============================================================
-- V3: Create Departments and Add Department-Based Workflow
-- ============================================================

-- ============================================================
-- 1. CREATE DEPARTMENTS TABLE
-- ============================================================

CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(150) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_departments_name
    ON departments(name);


-- ============================================================
-- 2. ADD DEPARTMENT TO USERS
-- ============================================================

ALTER TABLE users
    ADD COLUMN department_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id);

CREATE INDEX idx_users_department_id
    ON users(department_id);


-- ============================================================
-- 3. ADD DEPARTMENT TO TRANSITIONS
-- ============================================================

ALTER TABLE transitions
    ADD COLUMN department_id BIGINT;

ALTER TABLE transitions
    ADD CONSTRAINT fk_transitions_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id);

CREATE INDEX idx_transitions_department_id
    ON transitions(department_id);


-- ============================================================
-- 4. DOCUMENT THE RELATIONSHIPS
-- ============================================================
--
-- departments
--      1
--      |
--      |----< users
--
-- departments
--      1
--      |
--      |----< transitions
--
-- A user belongs to one department.
--
-- A department can contain many users.
--
-- A transition belongs to one department.
--
-- A department can have many transitions.
--
-- Workflow execution will later verify:
--
--     authenticated user
--             ↓
--       user's department
--             ↓
--       transition department
--             ↓
--          MATCH?
--             ↓
--       allow execution
--
-- ============================================================