CREATE INDEX idx_client_active_partial
    ON product (category_id, updated_at)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_updated_at
    ON product (updated_at);