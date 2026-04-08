CREATE TABLE IF NOT EXISTS inventory (
    inventory_id        VARCHAR(36)     NOT NULL,
    product_id          VARCHAR(36)     NOT NULL UNIQUE,
    product_name        VARCHAR(255)    NOT NULL,
    available_quantity  INT             NOT NULL,
    reserved_quantity   INT             NOT NULL DEFAULT 0,
    status              VARCHAR(50)     NOT NULL,
    PRIMARY KEY (inventory_id)
);

CREATE TABLE IF NOT EXISTS inventory_reservations (
    reservation_id  VARCHAR(36)     NOT NULL,
    inventory_id    VARCHAR(36)     NOT NULL,
    order_id        VARCHAR(36)     NOT NULL,
    quantity        INT             NOT NULL,
    status          VARCHAR(50)     NOT NULL,
    PRIMARY KEY (reservation_id),
    CONSTRAINT fk_inventory FOREIGN KEY (inventory_id)
        REFERENCES inventory(inventory_id)
);