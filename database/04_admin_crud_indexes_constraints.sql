SET timezone = 'Asia/Ho_Chi_Minh';

-- Admin CRUD support: keep high-volume list/filter/search paths predictable.
CREATE INDEX IF NOT EXISTS idx_districts_province_id ON districts(province_id);
CREATE INDEX IF NOT EXISTS idx_wards_district_id ON wards(district_id);
CREATE INDEX IF NOT EXISTS idx_users_creation_time ON users(creation_time DESC);
CREATE INDEX IF NOT EXISTS idx_users_full_name_lower ON users(LOWER(full_name));
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON user_role(role_id);
CREATE INDEX IF NOT EXISTS idx_branches_status_id ON branches(status_id);
CREATE INDEX IF NOT EXISTS idx_branches_ward_id ON branches(ward_id);
CREATE INDEX IF NOT EXISTS idx_rooms_branch_status ON rooms(branch_id, status_id);
CREATE INDEX IF NOT EXISTS idx_seats_room_position ON seats(room_id, y_position, x_position);
CREATE INDEX IF NOT EXISTS idx_product_branch_branch_status ON product_branch(branch_id, status_id);
CREATE INDEX IF NOT EXISTS idx_product_branch_product_id ON product_branch(product_id);
CREATE INDEX IF NOT EXISTS idx_movies_released_date ON movies(released_date DESC);
CREATE INDEX IF NOT EXISTS idx_movies_title_lower ON movies(LOWER(title));
CREATE INDEX IF NOT EXISTS idx_movie_images_movie_id ON movie_images(movie_id);
CREATE INDEX IF NOT EXISTS idx_movie_keyword_movie_id ON movie_keyword(movie_id);
CREATE INDEX IF NOT EXISTS idx_movie_keyword_keyword_id ON movie_keyword(keyword_id);
CREATE INDEX IF NOT EXISTS idx_movie_reviews_movie_created ON movie_reviews(movie_id, creation_time DESC);
CREATE INDEX IF NOT EXISTS idx_movie_reviews_user_created ON movie_reviews(user_id, creation_time DESC);
CREATE INDEX IF NOT EXISTS idx_movie_view_history_movie_start ON movie_view_history(movie_id, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_movie_cast_movie_order ON movie_cast(movie_id, cast_order);
CREATE INDEX IF NOT EXISTS idx_movie_cast_person_id ON movie_cast(person_id);
CREATE INDEX IF NOT EXISTS idx_movie_crew_movie_id ON movie_crew(movie_id);
CREATE INDEX IF NOT EXISTS idx_movie_crew_person_id ON movie_crew(person_id);
CREATE INDEX IF NOT EXISTS idx_movie_category_movie_id ON movie_category(movie_id);
CREATE INDEX IF NOT EXISTS idx_movie_category_category_id ON movie_category(category_id);
CREATE INDEX IF NOT EXISTS idx_showtimes_movie_start ON showtimes(movie_id, start_time);
CREATE INDEX IF NOT EXISTS idx_showtimes_room_start ON showtimes(room_id, start_time);
CREATE INDEX IF NOT EXISTS idx_showtimes_status_start ON showtimes(status_id, start_time);
CREATE INDEX IF NOT EXISTS idx_reservations_showtime_status_end ON reservations(showtime_id, status_id, end_time);
CREATE INDEX IF NOT EXISTS idx_reservations_user_start ON reservations(user_id, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_reservation_seat_reservation_id ON reservation_seat(reservation_id);
CREATE INDEX IF NOT EXISTS idx_reservation_seat_seat_id ON reservation_seat(seat_id);
CREATE INDEX IF NOT EXISTS idx_reservation_product_reservation_id ON reservation_product(reservation_id);
CREATE INDEX IF NOT EXISTS idx_tickets_reservation_id ON tickets(reservation_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_reservation_id ON payment_transactions(reservation_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_time ON payment_transactions(time DESC);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_status_time ON payment_transactions(status_id, time DESC);
CREATE UNIQUE INDEX IF NOT EXISTS idx_payment_transactions_transaction_id_unique ON payment_transactions(transaction_id);

-- Existing imported data has a few duplicate crew links; keep the earliest row.
WITH duplicate_crew AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY movie_id, person_id, position_id
               ORDER BY id
           ) AS row_number
    FROM movie_crew
)
DELETE FROM movie_crew mc
USING duplicate_crew dc
WHERE mc.id = dc.id
  AND dc.row_number > 1;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_user_role_user_role') THEN
        ALTER TABLE user_role ADD CONSTRAINT uk_user_role_user_role UNIQUE (user_id, role_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_role_permission_role_permission') THEN
        ALTER TABLE role_permission ADD CONSTRAINT uk_role_permission_role_permission UNIQUE (role_id, permission_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_rooms_branch_name') THEN
        ALTER TABLE rooms ADD CONSTRAINT uk_rooms_branch_name UNIQUE (branch_id, name);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_seats_room_name') THEN
        ALTER TABLE seats ADD CONSTRAINT uk_seats_room_name UNIQUE (room_id, name);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_seats_room_position') THEN
        ALTER TABLE seats ADD CONSTRAINT uk_seats_room_position UNIQUE (room_id, x_position, y_position);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_product_branch_branch_product') THEN
        ALTER TABLE product_branch ADD CONSTRAINT uk_product_branch_branch_product UNIQUE (branch_id, product_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_movie_keyword_movie_keyword') THEN
        ALTER TABLE movie_keyword ADD CONSTRAINT uk_movie_keyword_movie_keyword UNIQUE (movie_id, keyword_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_movie_category_movie_category') THEN
        ALTER TABLE movie_category ADD CONSTRAINT uk_movie_category_movie_category UNIQUE (movie_id, category_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_movie_cast_movie_person_character') THEN
        ALTER TABLE movie_cast ADD CONSTRAINT uk_movie_cast_movie_person_character UNIQUE (movie_id, person_id, "character");
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_movie_crew_movie_person_position') THEN
        ALTER TABLE movie_crew ADD CONSTRAINT uk_movie_crew_movie_person_position UNIQUE (movie_id, person_id, position_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_reservation_seat_reservation_seat') THEN
        ALTER TABLE reservation_seat ADD CONSTRAINT uk_reservation_seat_reservation_seat UNIQUE (reservation_id, seat_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_reservation_product_reservation_product') THEN
        ALTER TABLE reservation_product ADD CONSTRAINT uk_reservation_product_reservation_product UNIQUE (reservation_id, product_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_movies_duration_positive') THEN
        ALTER TABLE movies ADD CONSTRAINT ck_movies_duration_positive CHECK (duration > 0);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_products_price_nonnegative') THEN
        ALTER TABLE products ADD CONSTRAINT ck_products_price_nonnegative CHECK (price >= 0);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_room_types_extra_fee_nonnegative') THEN
        ALTER TABLE room_types ADD CONSTRAINT ck_room_types_extra_fee_nonnegative CHECK (extra_fee >= 0);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_seat_types_extra_fee_nonnegative') THEN
        ALTER TABLE seat_types ADD CONSTRAINT ck_seat_types_extra_fee_nonnegative CHECK (extra_fee >= 0);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_ticket_price_nonnegative') THEN
        ALTER TABLE ticket_price ADD CONSTRAINT ck_ticket_price_nonnegative CHECK (price >= 0);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_payment_transactions_amount_nonnegative') THEN
        ALTER TABLE payment_transactions ADD CONSTRAINT ck_payment_transactions_amount_nonnegative CHECK (amount >= 0);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_movie_reviews_rating_range') THEN
        ALTER TABLE movie_reviews ADD CONSTRAINT ck_movie_reviews_rating_range CHECK (rating BETWEEN 1 AND 5);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_ticket_price_time_range') THEN
        ALTER TABLE ticket_price ADD CONSTRAINT ck_ticket_price_time_range CHECK (time_range_start < time_range_end);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_reservations_time_range') THEN
        ALTER TABLE reservations ADD CONSTRAINT ck_reservations_time_range CHECK (start_time < end_time);
    END IF;
END $$;

ANALYZE;
