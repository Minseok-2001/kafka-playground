CREATE TABLE notification_request
    (
        id             BIGINT PRIMARY KEY,
        created_at     TIMESTAMP(6) NOT NULL,
        updated_at     TIMESTAMP(6) NOT NULL,
        deleted_at     TIMESTAMP(6) NULL,
        member_id      BIGINT       NOT NULL,
        channel        VARCHAR(32)  NOT NULL,
        subject        VARCHAR(200) NOT NULL,
        body           TEXT         NOT NULL,
        status         VARCHAR(32)  NOT NULL,
        scheduled_at   TIMESTAMP(6) NULL,
        sent_at        TIMESTAMP(6) NULL,
        failure_reason TEXT         NULL
    );

CREATE INDEX ix_notification_request_member ON notification_request (member_id);
CREATE INDEX ix_notification_request_status ON notification_request (status);
