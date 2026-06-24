-- V4__backfill_wallets_for_existing_users.sql
INSERT INTO wallets (user_id, balance, version, created_at, updated_at, created_by, updated_by)
SELECT u.id, 0.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM wallets w WHERE w.user_id = u.id
);