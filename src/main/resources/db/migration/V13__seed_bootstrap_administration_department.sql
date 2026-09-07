-- ============================================================
-- Seed the bootstrap Administration department
-- ============================================================
--
-- The database starts completely empty, but something has to be
-- able to log in and configure the workflow first. Rather than
-- seeding queues/actions/transitions (which would be exactly the
-- hard-coded workflow configuration this system is meant to avoid),
-- we seed a single Department named "Administration". It carries
-- no special column or flag — it is just a normal department row
-- that UserService recognizes by name when bootstrapping the very
-- first user (see V13's companion change in UserService).
-- ============================================================

INSERT INTO departments (name, description, created_at, updated_at)
VALUES (
    'Administration',
    'Reserved department for administering workflow configuration (departments, queues, actions, transitions, users).',
    now(),
    now()
);
