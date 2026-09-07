CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(500),
    image_url TEXT,

    current_queue_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_by BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_items_current_queue
        FOREIGN KEY (current_queue_id)
        REFERENCES queues(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_items_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_items_current_queue_id
    ON items(current_queue_id);

CREATE INDEX idx_items_status
    ON items(status);

CREATE INDEX idx_items_created_by
    ON items(created_by);
