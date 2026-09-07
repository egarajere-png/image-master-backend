CREATE TABLE item_transitions (
    id BIGSERIAL PRIMARY KEY,

    item_id BIGINT NOT NULL,
    transition_id BIGINT NOT NULL,
    performed_by BIGINT,

    source_queue_id BIGINT,
    destination_queue_id BIGINT,

    action_name VARCHAR(100),
    comment TEXT,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_item_transitions_item
        FOREIGN KEY (item_id)
        REFERENCES items(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_transitions_transition
        FOREIGN KEY (transition_id)
        REFERENCES transitions(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_item_transitions_performed_by
        FOREIGN KEY (performed_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_item_transitions_source_queue
        FOREIGN KEY (source_queue_id)
        REFERENCES queues(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_item_transitions_destination_queue
        FOREIGN KEY (destination_queue_id)
        REFERENCES queues(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_item_transitions_item_id
    ON item_transitions(item_id);

CREATE INDEX idx_item_transitions_transition_id
    ON item_transitions(transition_id);

CREATE INDEX idx_item_transitions_performed_by
    ON item_transitions(performed_by);

CREATE INDEX idx_item_transitions_created_at
    ON item_transitions(created_at);
