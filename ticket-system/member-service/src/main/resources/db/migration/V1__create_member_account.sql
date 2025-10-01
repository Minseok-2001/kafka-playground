CREATE TABLE member_account
    (
        id                   BIGINT PRIMARY KEY,
        created_at           TIMESTAMP(6) NOT NULL,
        updated_at           TIMESTAMP(6) NOT NULL,
        deleted_at           TIMESTAMP(6) NULL,
        email                VARCHAR(255) NOT NULL,
        nickname             VARCHAR(100) NOT NULL,
        status               VARCHAR(32)  NOT NULL,
        notification_channel JSON         NULL
    );

CREATE UNIQUE INDEX ux_member_account_email ON member_account (email);
