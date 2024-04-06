DROP TABLE IF EXISTS apps cascade;
DROP TABLE IF EXISTS blocked_apps cascade;
DROP TABLE IF EXISTS prices cascade;
DROP TABLE IF EXISTS currency_prices cascade;
DROP TABLE IF EXISTS users cascade;

CREATE TABLE apps (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    header_image TEXT NOT NULL,
    is_free BOOLEAN NOT NULL,
    short_description TEXT,
    developers TEXT,
    publishers TEXT,
    website VARCHAR(255),
    metacritic_score INT,
    metacritic_url VARCHAR(255),
    coming_soon BOOLEAN,
    release_date DATE
);

CREATE TABLE platforms (
    app_id BIGINT NOT NULL,
    platform VARCHAR(32) NOT NULL,
    FOREIGN KEY (app_id) REFERENCES apps (id)
);

CREATE TABLE prices (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    app_id BIGINT NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (app_id) REFERENCES apps (id)
);

CREATE TABLE currency_prices (
    price_id BIGINT NOT NULL,
    currency CHAR(3) NOT NULL check (currency in ('USD','EUR','RUB','KZT'),
    price BIGINT NOT NULL,
    FOREIGN KEY (price_id) REFERENCES prices (id)
);

CREATE TABLE blocked_apps (
    id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password CHAR(60) NOT NULL,
    role VARCHAR(255) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')
);

CREATE INDEX name_index ON apps (name);
CREATE INDEX email_index ON users (email);
