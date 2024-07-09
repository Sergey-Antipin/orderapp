-- liquibase formatted sql

-- changeset antipin:1720369119619-1
CREATE SEQUENCE IF NOT EXISTS order_seq START WITH 100 INCREMENT BY 1;

-- changeset antipin:1720369119619-2
CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 100 INCREMENT BY 1;

-- changeset antipin:1720369119619-3
CREATE TABLE order_items
(
    order_id BIGINT NOT NULL,
    item    VARCHAR(255)
);

-- changeset antipin:1720369119619-4
CREATE TABLE orders
(
    id     BIGINT NOT NULL DEFAULT nextval('order_seq'),
    amount DECIMAL,
    status VARCHAR(255),
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- changeset antipin:1720369119619-5
CREATE TABLE users
(
    id    BIGINT NOT NULL DEFAULT nextval('user_seq'),
    name  VARCHAR(255),
    email VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset antipin:1720369119619-6
CREATE TABLE users_orders
(
    user_id   BIGINT NOT NULL,
    order_id BIGINT NOT NULL
);

-- changeset antipin:1720369119619-7
ALTER TABLE users_orders
    ADD CONSTRAINT uc_users_orders_orders UNIQUE (order_id);

-- changeset antipin:1720369119619-8
ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_on_order FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset antipin:1720369119619-9
ALTER TABLE users_orders
    ADD CONSTRAINT fk_useord_on_order FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset antipin:1720369119619-10
ALTER TABLE users_orders
    ADD CONSTRAINT fk_useord_on_user FOREIGN KEY (user_id) REFERENCES users (id);

