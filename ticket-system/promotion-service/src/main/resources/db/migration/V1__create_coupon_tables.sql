CREATE TABLE coupon_policy
    (
        id              BIGINT PRIMARY KEY,
        created_at      TIMESTAMP(6)   NOT NULL,
        updated_at      TIMESTAMP(6)   NOT NULL,
        deleted_at      TIMESTAMP(6)   NULL,
        code            VARCHAR(64)    NOT NULL,
        name            VARCHAR(150)   NOT NULL,
        benefit_type    VARCHAR(32)    NOT NULL,
        benefit_value   DECIMAL(19, 2) NOT NULL,
        minimum_amount  DECIMAL(19, 2) NULL,
        valid_from      TIMESTAMP(6)   NOT NULL,
        valid_until     TIMESTAMP(6)   NOT NULL,
        total_quantity  INT            NULL,
        issued_quantity INT            NOT NULL DEFAULT 0
    );

CREATE UNIQUE INDEX ux_coupon_policy_code ON coupon_policy (code);

CREATE TABLE issued_coupon
    (
        id               BIGINT PRIMARY KEY,
        created_at       TIMESTAMP(6) NOT NULL,
        updated_at       TIMESTAMP(6) NOT NULL,
        deleted_at       TIMESTAMP(6) NULL,
        coupon_policy_id BIGINT       NOT NULL,
        member_id        BIGINT       NOT NULL,
        status           VARCHAR(32)  NOT NULL,
        redeemed_at      TIMESTAMP(6) NULL,
        expires_at       TIMESTAMP(6) NOT NULL,
        CONSTRAINT fk_issued_coupon_policy FOREIGN KEY (coupon_policy_id) REFERENCES coupon_policy (id)
    );

CREATE INDEX ix_issued_coupon_member ON issued_coupon (member_id);
CREATE INDEX ix_issued_coupon_policy ON issued_coupon (coupon_policy_id);
