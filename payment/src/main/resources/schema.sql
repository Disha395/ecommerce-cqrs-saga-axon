CREATE TABLE IF NOT EXISTS payments (
    payment_id      VARCHAR(36)     NOT NULL,
    order_id        VARCHAR(36)     NOT NULL UNIQUE,
    customer_id     VARCHAR(36)     NOT NULL,
    amount          DECIMAL(19, 4)  NOT NULL,
    status          VARCHAR(50)     NOT NULL,
    failure_reason  VARCHAR(255),
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    PRIMARY KEY (payment_id)
);