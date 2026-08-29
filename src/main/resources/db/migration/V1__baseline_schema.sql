CREATE EXTENSION IF NOT EXISTS postgis;

CREATE SEQUENCE IF NOT EXISTS app_user_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS rider_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS driver_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ride_request_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ride_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS payment_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS rating_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS wallet_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS wallet_transactions_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS platform_commission_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    roles VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, roles)
);

CREATE TABLE IF NOT EXISTS rider (
    id BIGINT PRIMARY KEY,
    user_id BIGINT REFERENCES app_user(id),
    rating DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS driver (
    id BIGINT PRIMARY KEY,
    user_id BIGINT REFERENCES app_user(id),
    rating DOUBLE PRECISION,
    status VARCHAR(255),
    current_location geometry(Point, 4326),
    vehicle_id VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS ride_request (
    id BIGINT PRIMARY KEY,
    pickup_location geometry(Point, 4326),
    drop_off_location geometry(Point, 4326),
    requested_time TIMESTAMP,
    rider_id BIGINT REFERENCES rider(id),
    payment_method VARCHAR(255),
    ride_request_status VARCHAR(255),
    fare DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS ride (
    id BIGINT PRIMARY KEY,
    pickup_location geometry(Point, 4326),
    drop_off_location geometry(Point, 4326),
    created_time TIMESTAMP,
    rider_id BIGINT REFERENCES rider(id),
    driver_id BIGINT REFERENCES driver(id),
    payment_method VARCHAR(255),
    ride_status VARCHAR(255),
    fare DOUBLE PRECISION,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    otp VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payment (
    id BIGINT PRIMARY KEY,
    payment_method VARCHAR(255),
    ride_id BIGINT UNIQUE REFERENCES ride(id),
    amount DOUBLE PRECISION,
    payment_status VARCHAR(255),
    payment_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rating (
    id BIGINT PRIMARY KEY,
    ride_id BIGINT REFERENCES ride(id),
    rider_id BIGINT REFERENCES rider(id),
    driver_id BIGINT REFERENCES driver(id),
    driver_rating INTEGER,
    rider_rating INTEGER
);

CREATE TABLE IF NOT EXISTS wallet (
    id BIGINT PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES app_user(id),
    balance DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGINT PRIMARY KEY,
    amount DOUBLE PRECISION,
    transaction_type VARCHAR(255),
    transaction_method VARCHAR(255),
    ride_id BIGINT REFERENCES ride(id),
    transaction_id VARCHAR(255),
    wallet_id BIGINT REFERENCES wallet(id),
    time_stamp TIMESTAMP
);

CREATE TABLE IF NOT EXISTS platform_commission (
    id BIGINT PRIMARY KEY,
    payment_id BIGINT NOT NULL UNIQUE REFERENCES payment(id),
    amount NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ride_req_rider ON ride_request(rider_id);
CREATE INDEX IF NOT EXISTS idx_ride_rider ON ride(rider_id);
CREATE INDEX IF NOT EXISTS idx_ride_driver ON ride(driver_id);
CREATE INDEX IF NOT EXISTS idx_rating_rider ON rating(rider_id);
CREATE INDEX IF NOT EXISTS idx_rating_driver ON rating(driver_id);
CREATE INDEX IF NOT EXISTS idx_wallet_transaction_wallet ON wallet_transactions(wallet_id);
CREATE INDEX IF NOT EXISTS idx_wallet_transaction_ride ON wallet_transactions(ride_id);
