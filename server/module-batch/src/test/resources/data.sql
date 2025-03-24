CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    is_subscription BOOLEAN
);

CREATE TABLE order_item (
    order_id BIGINT,
    payment_date DATE
);