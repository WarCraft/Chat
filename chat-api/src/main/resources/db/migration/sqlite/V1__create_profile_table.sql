CREATE TABLE profile
(
    player_id CHARACTER(32) PRIMARY KEY,
    name      VARCHAR(16) NOT NULL,
    tag       VARCHAR(32),
    home      VARCHAR(32)
);
