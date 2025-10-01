CREATE TABLE ticket_reservation
    (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        member_id   BIGINT       NOT NULL,
        seat_number VARCHAR(50)  NOT NULL,
        status      VARCHAR(32)  NOT NULL,
        created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
        updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
        deleted_at  TIMESTAMP(6) NULL
    );

CREATE INDEX idx_ticket_reservation_member ON ticket_reservation (member_id);
