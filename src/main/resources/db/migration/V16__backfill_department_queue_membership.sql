-- ============================================================
-- V16: Backfill queue membership for already-assigned users
-- ============================================================
--
-- From this point on, DepartmentService.assignUser() automatically
-- adds a user to every queue in the department they're assigned
-- to (and removes them from their old department's queues if
-- they're being moved). That automation didn't exist before now,
-- so any user who was already assigned a department is missing
-- the QueueUser rows they'd have gotten if it had.
--
-- This is purely additive — it only INSERTs missing membership
-- rows for a user's *current* department. It does not remove any
-- existing membership, including memberships that may be stale
-- leftovers from before a user's department was last changed. If
-- you want those cleaned up too, run this manually afterwards
-- (NOT included here, since it's a DELETE and worth reviewing
-- first):
--
--   DELETE FROM queue_users qu
--   USING queues q, users u
--   WHERE qu.queue_id = q.id
--     AND qu.user_id = u.id
--     AND q.department_id IS NOT NULL
--     AND (u.department_id IS NULL OR q.department_id <> u.department_id);

INSERT INTO queue_users (queue_id, user_id, created_at)
SELECT q.id, u.id, now()
FROM queues q
JOIN users u ON u.department_id = q.department_id
WHERE q.department_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM queue_users qu
      WHERE qu.queue_id = q.id
        AND qu.user_id = u.id
  );