--liquibase formatted sql

--changeset dev:002
ALTER TABLE tasks ADD COLUMN created_at TIMESTAMP DEFAULT NOW();

--rollback ALTER TABLE tasks DROP COLUMN created_at;