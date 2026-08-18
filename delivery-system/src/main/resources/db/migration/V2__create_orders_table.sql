-- V2__create_orders_tables.sql
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES users(id),
    courier_id UUID REFERENCES users(id),
    origin_street VARCHAR(255) NOT NULL,
    origin_city VARCHAR(100) NOT NULL,
    origin_state VARCHAR(100),
    origin_zip_code VARCHAR(20) NOT NULL,
    origin_country VARCHAR(100) NOT NULL,
    destination_street VARCHAR(255) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    destination_state VARCHAR(100),
    destination_zip_code VARCHAR(20) NOT NULL,
    destination_country VARCHAR(100) NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    dimensions VARCHAR(50),
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    price DECIMAL(10,2) NOT NULL,
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP,
    version INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    changed_by_user_id UUID REFERENCES users(id),
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );