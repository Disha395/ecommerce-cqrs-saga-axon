CREATE TABLE IF NOT EXISTS orders (
    order_id        VARCHAR(36)     NOT NULL,
    customer_id     VARCHAR(36)     NOT NULL,
    shipping_address VARCHAR(255)   NOT NULL,
    total_amount    DECIMAL(19, 4)  NOT NULL,
    status          VARCHAR(50)     NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    PRIMARY KEY (order_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    item_id         VARCHAR(36)     NOT NULL,
    order_id        VARCHAR(36)     NOT NULL,
    product_id      VARCHAR(36)     NOT NULL,
    product_name    VARCHAR(255)    NOT NULL,
    quantity        INT             NOT NULL,
    unit_price      DECIMAL(19, 4)  NOT NULL,
    sub_total       DECIMAL(19, 4)  NOT NULL,
    PRIMARY KEY (item_id),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
);