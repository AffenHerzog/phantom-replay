CREATE TABLE IF NOT EXISTS phantom_players
(
    uuid      VARCHAR(36) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS phantom_replays
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    data LONGBLOB    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uuid) REFERENCES phantom_players (uuid) ON DELETE CASCADE
);