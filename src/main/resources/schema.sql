CREATE TABLE IF NOT EXISTS phantom_players
(
    uuid      VARCHAR(36) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS phantom_replays
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    player_uuid VARCHAR(36) NOT NULL,
    replay_data LONGBLOB    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_uuid) REFERENCES phantom_players (uuid) ON DELETE CASCADE
);