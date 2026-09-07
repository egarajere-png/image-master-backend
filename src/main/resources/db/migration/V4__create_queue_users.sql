CREATE TABLE queue_users (
    id BIGSERIAL PRIMARY KEY,
    queue_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_queue_users_queue
        FOREIGN KEY (queue_id)
        REFERENCES queues(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_queue_users_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_queue_users_queue_user
        UNIQUE (queue_id, user_id)
);

CREATE INDEX idx_queue_users_queue_id
    ON queue_users(queue_id);

CREATE INDEX idx_queue_users_user_id
    ON queue_users(user_id);
