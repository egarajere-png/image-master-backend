CREATE TABLE queue_actions (
    id BIGSERIAL PRIMARY KEY,
    queue_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_queue_actions_queue
        FOREIGN KEY (queue_id)
        REFERENCES queues(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_queue_actions_action
        FOREIGN KEY (action_id)
        REFERENCES actions(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_queue_actions_queue_action
        UNIQUE (queue_id, action_id)
);

CREATE INDEX idx_queue_actions_queue_id
    ON queue_actions(queue_id);

CREATE INDEX idx_queue_actions_action_id
    ON queue_actions(action_id);
