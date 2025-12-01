CREATE SEQUENCE IF NOT EXISTS meeting_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MAXVALUE;

CREATE TABLE meetings
(
    id           BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('meeting_id_seq'),
    title        VARCHAR(255)       NOT NULL,
    description  VARCHAR(255)       NOT NULL,
    organizer_id BIGINT             NOT NULL,
    CONSTRAINT fk_meeting_organizer FOREIGN KEY (organizer_id) REFERENCES users (id)
);

CREATE TABLE meetings_participants
(
    meeting_id      BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    PRIMARY KEY (meeting_id, participant_id),
    CONSTRAINT fk_meeting_id FOREIGN KEY (meeting_id) REFERENCES meetings (id) ON DELETE CASCADE,
    CONSTRAINT fk_participant_id FOREIGN KEY (participant_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX idx_user_email ON users (email);

CREATE INDEX idx_availability_search ON availabilities (owner_id, availability_status, start_date_time, end_date_time);

ALTER TABLE availabilities ADD COLUMN version INTEGER NOT NULL DEFAULT 0;

INSERT INTO users (first_name, last_name, email)
values ('FirstName', 'LastName', 'user2@gmail.com');
