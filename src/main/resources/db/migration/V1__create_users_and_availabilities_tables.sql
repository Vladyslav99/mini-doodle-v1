CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MAXVALUE;

CREATE TABLE users
(
    id         BIGINT PRIMARY KEY       NOT NULL DEFAULT nextval('user_id_seq'),
    first_name VARCHAR(255)             NOT NULL,
    last_name  VARCHAR(255)             NOT NULL,
    email      VARCHAR(255)             NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (first_name, last_name, email)
values ('FirstName', 'LastName', 'user1@gmail.com');

CREATE SEQUENCE availability_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MAXVALUE;

CREATE TYPE availability_status AS ENUM (
    'FREE',
    'BUSY'
);

CREATE TABLE availabilities
(
    id                  BIGINT PRIMARY KEY       NOT NULL DEFAULT nextval('availability_id_seq'),
    start_date_time     TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date_time       TIMESTAMP WITH TIME ZONE NOT NULL,
    availability_status availability_status      NOT NULL DEFAULT 'FREE',
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE availabilities
    ADD COLUMN owner_id BIGINT NOT NULL REFERENCES users (id);
