-- ============================================================
-- QUEUES
-- ============================================================

INSERT INTO queues (name, description)
VALUES
    ('TELLER', 'Teller processing queue'),
    ('BRANCH_MANAGER', 'Branch Manager approval queue'),
    ('CBO1', 'CBO1 processing queue'),
    ('CBO2', 'CBO2 final approval queue');


-- ============================================================
-- ACTIONS
-- ============================================================

INSERT INTO actions (name, description)
VALUES
    ('UPLOAD', 'Upload a new item'),
    ('AMEND', 'Amend an existing item'),
    ('APPROVE', 'Approve the item'),
    ('DECLINE', 'Decline and remove the item from active workflow'),
    ('RETURN', 'Return the item to the previous processing queue'),
    ('ACCEPT', 'Accept the item for processing'),
    ('PROCESS', 'Process the item'),
    ('REJECT', 'Reject the item back to the Teller');


-- ============================================================
-- QUEUE ACTIONS
-- ============================================================

INSERT INTO queue_actions (queue_id, action_id)
SELECT q.id, a.id
FROM queues q
CROSS JOIN actions a
WHERE q.name = 'TELLER'
  AND a.name IN ('UPLOAD', 'AMEND');


INSERT INTO queue_actions (queue_id, action_id)
SELECT q.id, a.id
FROM queues q
CROSS JOIN actions a
WHERE q.name = 'BRANCH_MANAGER'
  AND a.name IN ('APPROVE', 'DECLINE', 'RETURN');


INSERT INTO queue_actions (queue_id, action_id)
SELECT q.id, a.id
FROM queues q
CROSS JOIN actions a
WHERE q.name = 'CBO1'
  AND a.name IN ('ACCEPT', 'PROCESS', 'REJECT', 'RETURN');


INSERT INTO queue_actions (queue_id, action_id)
SELECT q.id, a.id
FROM queues q
CROSS JOIN actions a
WHERE q.name = 'CBO2'
  AND a.name IN ('APPROVE', 'RETURN', 'DECLINE');


-- ============================================================
-- TRANSITIONS
-- ============================================================

-- ------------------------------------------------------------
-- TELLER -> BRANCH MANAGER
-- UPLOAD
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'Teller uploads an item to Branch Manager'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'BRANCH_MANAGER'
WHERE source_queue.name = 'TELLER'
  AND a.name = 'UPLOAD';


-- ------------------------------------------------------------
-- TELLER -> BRANCH MANAGER
-- AMEND
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'Teller amends an item and sends it back to Branch Manager'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'BRANCH_MANAGER'
WHERE source_queue.name = 'TELLER'
  AND a.name = 'AMEND';


-- ------------------------------------------------------------
-- BRANCH MANAGER -> CBO1
-- APPROVE
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'Branch Manager approves item for CBO1'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'CBO1'
WHERE source_queue.name = 'BRANCH_MANAGER'
  AND a.name = 'APPROVE';


-- ------------------------------------------------------------
-- BRANCH MANAGER -> TELLER
-- RETURN
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'Branch Manager returns item to Teller'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'TELLER'
WHERE source_queue.name = 'BRANCH_MANAGER'
  AND a.name = 'RETURN';


-- ------------------------------------------------------------
-- BRANCH MANAGER -> REMOVED
-- DECLINE
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    outcome,
    description
)
SELECT
    qa.id,
    'REMOVED',
    'Branch Manager declines and removes item from active workflow'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
WHERE source_queue.name = 'BRANCH_MANAGER'
  AND a.name = 'DECLINE';


-- ------------------------------------------------------------
-- CBO1 -> CBO2
-- ACCEPT
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'CBO1 accepts item and sends it to CBO2'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'CBO2'
WHERE source_queue.name = 'CBO1'
  AND a.name = 'ACCEPT';


-- ------------------------------------------------------------
-- CBO1 -> CBO2
-- PROCESS
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'CBO1 processes item and sends it to CBO2'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'CBO2'
WHERE source_queue.name = 'CBO1'
  AND a.name = 'PROCESS';


-- ------------------------------------------------------------
-- CBO1 -> TELLER
-- REJECT
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'CBO1 rejects item and sends it back to Teller'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'TELLER'
WHERE source_queue.name = 'CBO1'
  AND a.name = 'REJECT';


-- ------------------------------------------------------------
-- CBO1 -> BRANCH MANAGER
-- RETURN
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'CBO1 returns item to Branch Manager'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'BRANCH_MANAGER'
WHERE source_queue.name = 'CBO1'
  AND a.name = 'RETURN';


-- ------------------------------------------------------------
-- CBO2 -> COMPLETED
-- APPROVE
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    outcome,
    description
)
SELECT
    qa.id,
    'COMPLETED',
    'CBO2 approves and completes the workflow'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
WHERE source_queue.name = 'CBO2'
  AND a.name = 'APPROVE';


-- ------------------------------------------------------------
-- CBO2 -> CBO1
-- RETURN
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    destination_queue_id,
    description
)
SELECT
    qa.id,
    destination.id,
    'CBO2 returns item to CBO1'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
JOIN queues destination
    ON destination.name = 'CBO1'
WHERE source_queue.name = 'CBO2'
  AND a.name = 'RETURN';


-- ------------------------------------------------------------
-- CBO2 -> REMOVED
-- DECLINE
-- ------------------------------------------------------------

INSERT INTO transitions (
    queue_action_id,
    outcome,
    description
)
SELECT
    qa.id,
    'REMOVED',
    'CBO2 declines and removes item from active workflow'
FROM queue_actions qa
JOIN queues source_queue
    ON source_queue.id = qa.queue_id
JOIN actions a
    ON a.id = qa.action_id
WHERE source_queue.name = 'CBO2'
  AND a.name = 'DECLINE';

