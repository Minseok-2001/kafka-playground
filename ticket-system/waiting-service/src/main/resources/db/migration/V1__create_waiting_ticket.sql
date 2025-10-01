CREATE TABLE waiting_ticket
    (
        id              BIGINT PRIMARY KEY,
        created_at      TIMESTAMP(6) NOT NULL,
        updated_at      TIMESTAMP(6) NOT NULL,
        deleted_at      TIMESTAMP(6) NULL,
        queue_code      VARCHAR(64)  NOT NULL,
        member_id       BIGINT       NOT NULL,
        status          VARCHAR(32)  NOT NULL,
        issued_sequence BIGINT       NOT NULL,
        expired_at      TIMESTAMP(6) NOT NULL
    );

CREATE INDEX ix_waiting_ticket_queue_status ON waiting_ticket (queue_code, status);
CREATE INDEX ix_waiting_ticket_member ON waiting_ticket (member_id, queue_code);
