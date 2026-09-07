CREATE TABLE transitions (
    id BIGSERIAL PRIMARY KEY,
    queue_action_id BIGINT NOT NULL,
    destination_queue_id BIGINT,
    outcome VARCHAR(30),
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transitions_queue_action
        FOREIGN KEY (queue_action_id)
        REFERENCES queue_actions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transitions_destination_queue
        FOREIGN KEY (destination_queue_id)
        REFERENCES queues(id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_transitions_destination_or_outcome
        CHECK (
            destination_queue_id IS NOT NULL
            OR outcome IS NOT NULL
        )
);

CREATE INDEX idx_transitions_queue_action_id
    ON transitions(queue_action_id);

CREATE INDEX idx_transitions_destination_queue_id
    ON transitions(destination_queue_id);
