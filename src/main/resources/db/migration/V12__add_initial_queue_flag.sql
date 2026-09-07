-- ============================================================
-- Add configurable initial queue support
-- ============================================================

ALTER TABLE queues
ADD COLUMN is_initial BOOLEAN NOT NULL DEFAULT FALSE;


-- Only one queue can be configured as the initial queue.
CREATE UNIQUE INDEX ux_queues_single_initial
ON queues (is_initial)
WHERE is_initial = TRUE;