-- ============================================================
-- V14: Department-scoped queues/items + item identity fields
-- ============================================================
--
-- Two independent changes, both requested by the business:
--
-- 1. Branches (departments) must run fully separate workflow
--    queues. A Teller in Westlands and a Teller in Koinange are
--    both "Tellers" but must never share a physical queue, so an
--    item created in one branch is never visible to the other.
--    This adds department_id to `queues`, matching the entity
--    (Queue.department) which already existed in code without a
--    backing column.
--
-- 2. Every item is "rooted" in the department of the Teller who
--    created it, for the same isolation reason. This adds
--    department_id to `items`, matching Item.department, plus the
--    id number / customer name / phone number that must now
--    accompany every uploaded image.
--
-- ============================================================


-- ------------------------------------------------------------
-- 1. QUEUES: add department_id
-- ------------------------------------------------------------

ALTER TABLE queues
    ADD COLUMN department_id BIGINT;

ALTER TABLE queues
    ADD CONSTRAINT fk_queues_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id);

CREATE INDEX idx_queues_department_id
    ON queues(department_id);


-- The old constraint only allowed ONE initial queue globally.
-- Each department now needs its own initial queue (its own
-- "front door" where new items enter that branch's workflow).
DROP INDEX IF EXISTS ux_queues_single_initial;

CREATE UNIQUE INDEX ux_queues_single_initial_per_department
    ON queues (department_id)
    WHERE is_initial = TRUE;


-- ------------------------------------------------------------
-- 2. ITEMS: add department_id + identity fields
-- ------------------------------------------------------------

ALTER TABLE items
    ADD COLUMN department_id BIGINT;

ALTER TABLE items
    ADD CONSTRAINT fk_items_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id);

CREATE INDEX idx_items_department_id
    ON items(department_id);

ALTER TABLE items
    ADD COLUMN id_number VARCHAR(50);

ALTER TABLE items
    ADD COLUMN customer_name VARCHAR(150);

ALTER TABLE items
    ADD COLUMN phone_number VARCHAR(30);

CREATE INDEX idx_items_id_number
    ON items(id_number);

CREATE INDEX idx_items_customer_name
    ON items(customer_name);


-- ------------------------------------------------------------
-- NOTE FOR EXISTING DATA
-- ------------------------------------------------------------
-- Any queues seeded before this migration (e.g. the original
-- TELLER / BRANCH_MANAGER / CBO1 / CBO2 rows) will have a NULL
-- department_id after this runs. They will not be visible to any
-- non-admin user, and won't be usable as an initial queue, until
-- an Administrator assigns them a department (or creates fresh,
-- per-branch queues) from Admin > Queues.
