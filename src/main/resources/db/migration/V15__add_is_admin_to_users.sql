-- ============================================================
-- V15: Add missing is_admin column to users
-- ============================================================
--
-- User.isAdmin has always been mapped to a users.is_admin
-- column, but no prior migration ever created it. This was a
-- pre-existing gap (unrelated to the V14 department work) that
-- only surfaced once Hibernate schema validation ran against a
-- truly fresh database.
--
-- The first user to ever sign in is automatically made an admin
-- (see UserService), so this defaults everyone to false and lets
-- that bootstrap logic take over from here.

ALTER TABLE users
    ADD COLUMN is_admin BOOLEAN NOT NULL DEFAULT FALSE;
