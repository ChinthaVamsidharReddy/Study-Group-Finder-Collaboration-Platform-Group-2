-- Message status table for delivered/read receipts
CREATE TABLE IF NOT EXISTS message_status (
    message_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    delivered_at DATETIME NULL,
    read_at      DATETIME NULL,
    PRIMARY KEY (message_id, user_id)
);

-- Message reactions table for emoji reacts
CREATE TABLE IF NOT EXISTS message_reaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message_id BIGINT NOT NULL,
    emoji VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_message (message_id)
);


