-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Teams
CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    short_name VARCHAR(10) NOT NULL,
    stadium VARCHAR(100) NOT NULL,
    logo_url VARCHAR(255)
);

-- Games
CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    home_team_id BIGINT NOT NULL REFERENCES teams(id),
    away_team_id BIGINT NOT NULL REFERENCES teams(id),
    game_date_time TIMESTAMP NOT NULL,
    stadium VARCHAR(100) NOT NULL,
    sale_start_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_date ON games(game_date_time);
CREATE INDEX idx_games_status ON games(status);

-- Sections
CREATE TABLE sections (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL REFERENCES games(id),
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL
);

CREATE INDEX idx_sections_game ON sections(game_id);

-- Seats
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT NOT NULL REFERENCES sections(id),
    row VARCHAR(5) NOT NULL,
    number INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_seats_section ON seats(section_id);
CREATE INDEX idx_seats_status ON seats(status);

-- Reservations
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    game_id BIGINT NOT NULL REFERENCES games(id),
    total_price NUMERIC(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    qr_code VARCHAR(36) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    cancelled_at TIMESTAMP
);

CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_game ON reservations(game_id);

-- Reservation Seats
CREATE TABLE reservation_seats (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservations(id),
    seat_id BIGINT NOT NULL REFERENCES seats(id),
    UNIQUE(seat_id)
);

-- Payments
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE REFERENCES reservations(id),
    amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    method VARCHAR(20) NOT NULL,
    pg_transaction_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    paid_at TIMESTAMP
);
