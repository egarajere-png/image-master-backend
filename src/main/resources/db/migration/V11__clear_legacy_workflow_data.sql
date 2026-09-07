-- ============================================================
-- Clear legacy Image Master workflow data
-- ============================================================
--
-- This migration removes the previously seeded workflow
-- configuration and all dependent operational records.
--
-- The database schema remains unchanged.
--
-- After this migration, workflow configuration will be created
-- dynamically through the Image Master application.
-- ============================================================


-- ------------------------------------------------------------
-- 1. Remove workflow history
-- ------------------------------------------------------------

DELETE FROM item_transitions;


-- ------------------------------------------------------------
-- 2. Remove uploaded/processed items
-- ------------------------------------------------------------

DELETE FROM items;


-- ------------------------------------------------------------
-- 3. Remove workflow transitions
-- ------------------------------------------------------------

DELETE FROM transitions;


-- ------------------------------------------------------------
-- 4. Remove queue/action relationships
-- ------------------------------------------------------------

DELETE FROM queue_actions;


-- ------------------------------------------------------------
-- 5. Remove user/queue memberships
-- ------------------------------------------------------------

DELETE FROM queue_users;


-- ------------------------------------------------------------
-- 6. Remove workflow queues
-- ------------------------------------------------------------

DELETE FROM queues;


-- ------------------------------------------------------------
-- 7. Remove workflow actions
-- ------------------------------------------------------------

DELETE FROM actions;


-- ------------------------------------------------------------
-- 8. Remove application users
-- ------------------------------------------------------------

DELETE FROM users;


-- ------------------------------------------------------------
-- 9. Remove departments
-- ------------------------------------------------------------

DELETE FROM departments;


-- ============================================================
-- Reset PostgreSQL identity sequences
-- ============================================================

ALTER SEQUENCE users_id_seq RESTART WITH 1;

ALTER SEQUENCE departments_id_seq RESTART WITH 1;

ALTER SEQUENCE queues_id_seq RESTART WITH 1;

ALTER SEQUENCE actions_id_seq RESTART WITH 1;

ALTER SEQUENCE queue_users_id_seq RESTART WITH 1;

ALTER SEQUENCE queue_actions_id_seq RESTART WITH 1;

ALTER SEQUENCE transitions_id_seq RESTART WITH 1;

ALTER SEQUENCE items_id_seq RESTART WITH 1;

ALTER SEQUENCE item_transitions_id_seq RESTART WITH 1;