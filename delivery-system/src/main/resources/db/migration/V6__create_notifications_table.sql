-- V6__create_notifications_table.sql

CREATE TABLE IF NOT EXISTS notifications(
  id UUID PRIMARY KEY  DEFAULT gen_random_uuid(),
    recipient_id UUID NOT NULL REFERENCES users(id),
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL,
    reference_id UUID,
    is_read BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);