--liquibase formatted sql

--changeset severin:1
INSERT INTO users (username, email, role)
VALUES ('user1', 'user1@gmail.com', 'ROLE_USER'),
       ('admin1', 'admin1@gmail.com', 'ROLE_SUPERVISOR'),
       ('mentor1', 'mentor1gmail.com', 'ROLE_MENTOR');

