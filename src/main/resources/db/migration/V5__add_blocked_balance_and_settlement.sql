-- V5__add_blocked_balance_and_settlement.sql

-- Add blocked_balance column to wallets
ALTER TABLE wallets ADD COLUMN IF NOT EXISTS blocked_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00;


-- Create settlement_requests table
CREATE TABLE IF NOT EXISTS settlement_requests(
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    courier_id UUID NOT NULL REFERENCES users(id),
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processed_by UUID REFERENCES users(id),
    processed_at TIMESTAMP,
    note VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);