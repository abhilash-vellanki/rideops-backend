ALTER TABLE app_user ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE rider ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE driver ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE ride_request ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE ride ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE payment ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE rating ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE wallet ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE app_user
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN password SET NOT NULL;

UPDATE rider SET rating = 0 WHERE rating IS NULL;
ALTER TABLE rider ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE rider ALTER COLUMN rating SET NOT NULL;
ALTER TABLE rider ADD CONSTRAINT uk_rider_user UNIQUE (user_id);

UPDATE driver SET rating = 0 WHERE rating IS NULL;
UPDATE driver SET status = 'OFFLINE' WHERE status IS NULL;
ALTER TABLE driver ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE driver ALTER COLUMN rating SET NOT NULL;
ALTER TABLE driver ALTER COLUMN status SET NOT NULL;
ALTER TABLE driver ALTER COLUMN vehicle_id SET NOT NULL;
ALTER TABLE driver ADD CONSTRAINT uk_driver_user UNIQUE (user_id);

ALTER TABLE ride_request
    ALTER COLUMN pickup_location SET NOT NULL,
    ALTER COLUMN drop_off_location SET NOT NULL,
    ALTER COLUMN rider_id SET NOT NULL,
    ALTER COLUMN payment_method SET NOT NULL,
    ALTER COLUMN ride_request_status SET NOT NULL,
    ALTER COLUMN fare TYPE NUMERIC(19, 2) USING ROUND(fare::numeric, 2),
    ALTER COLUMN fare SET NOT NULL;

ALTER TABLE ride
    ALTER COLUMN pickup_location SET NOT NULL,
    ALTER COLUMN drop_off_location SET NOT NULL,
    ALTER COLUMN rider_id SET NOT NULL,
    ALTER COLUMN driver_id SET NOT NULL,
    ALTER COLUMN payment_method SET NOT NULL,
    ALTER COLUMN ride_status SET NOT NULL,
    ALTER COLUMN fare TYPE NUMERIC(19, 2) USING ROUND(fare::numeric, 2),
    ALTER COLUMN fare SET NOT NULL;

ALTER TABLE payment
    ALTER COLUMN payment_method SET NOT NULL,
    ALTER COLUMN ride_id SET NOT NULL,
    ALTER COLUMN payment_status SET NOT NULL,
    ALTER COLUMN amount TYPE NUMERIC(19, 2) USING ROUND(amount::numeric, 2),
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE wallet
    ALTER COLUMN user_id SET NOT NULL,
    ALTER COLUMN balance TYPE NUMERIC(19, 2) USING ROUND(balance::numeric, 2),
    ALTER COLUMN balance SET DEFAULT 0,
    ALTER COLUMN balance SET NOT NULL;

ALTER TABLE wallet_transactions
    ALTER COLUMN amount TYPE NUMERIC(19, 2) USING ROUND(amount::numeric, 2),
    ALTER COLUMN amount SET NOT NULL,
    ALTER COLUMN transaction_type SET NOT NULL,
    ALTER COLUMN transaction_method SET NOT NULL,
    ALTER COLUMN wallet_id SET NOT NULL;

ALTER TABLE rating
    ALTER COLUMN ride_id SET NOT NULL,
    ALTER COLUMN rider_id SET NOT NULL,
    ALTER COLUMN driver_id SET NOT NULL,
    ADD CONSTRAINT uk_rating_ride UNIQUE (ride_id),
    ADD CONSTRAINT chk_driver_rating CHECK (driver_rating BETWEEN 1 AND 5),
    ADD CONSTRAINT chk_rider_rating CHECK (rider_rating BETWEEN 1 AND 5);

CREATE INDEX idx_driver_available_location_geography
    ON driver USING GIST ((current_location::geography))
    WHERE status = 'AVAILABLE' AND current_location IS NOT NULL;

CREATE SEQUENCE refresh_token_session_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE refresh_token_session (
    id BIGINT PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    replaced_by_token_hash VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_refresh_token_session_user_active
    ON refresh_token_session(user_id)
    WHERE revoked_at IS NULL;
