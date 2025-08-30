--liquibase formatted sql

--changeset severin:1
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
    );
CREATE TABLE user_roles
(
    user_id BIGINT      NOT NULL,
    roles   VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);