ALTER TABLE orders
    ALTER COLUMN customer_id SET NOT NULL,
    DROP COLUMN customer_full_name,
    DROP COLUMN customer_address,
    DROP COLUMN customer_phone;

ALTER TABLE order_items
    ALTER COLUMN product_id SET NOT NULL,
    DROP COLUMN product_name,
    DROP COLUMN product_price;
