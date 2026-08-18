-- V3__create_wallets_and_transactions.sql
CREATE TABLE  IF NOT EXISTS wallets(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    version INTEGER NOT NULL DEFAULT 0,
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS transactions(
    id BIGSERIAL PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    amount DECIMAL(12,2) NOT NULL,
    type VARCHAR(30) NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);