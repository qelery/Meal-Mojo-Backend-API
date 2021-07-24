CREATE TYPE ISO_DOW AS ENUM (
    'MONDAY' , 'TUESDAY' , 'WEDNESDAY' , 'THURSDAY' , 'FRIDAY' , 'SATURDAY' , 'SUNDAY'
    );

CREATE TYPE PAYMENT_METHOD AS ENUM (
    'CARD', 'APPLE_PAY' , 'PAYPAL'
    );

CREATE TYPE PURCHASE_STATUS AS ENUM (
    'CART', 'PURCHASED'
    );

CREATE TYPE ROLE AS ENUM (
    'CUSTOMER', 'MERCHANT'
    );

CREATE TYPE STATE AS ENUM (
    'AK', 'AL', 'AR', 'AZ', 'CA',
    'CO', 'CT', 'DE', 'FL', 'GA',
    'HI', 'IA', 'ID', 'IL', 'IN',
    'KS', 'KY', 'LA', 'MA', 'MD',
    'ME', 'MI', 'MN', 'MO', 'MS',
    'MT', 'NC', 'ND', 'NE', 'NH',
    'NJ', 'NM', 'NV', 'NY', 'OH',
    'OK', 'OR', 'PA', 'RI', 'SC',
    'SD', 'TN', 'TX', 'UT', 'VA',
    'VT', 'WA', 'WI', 'WV', 'WY',
    'DC'
    );

CREATE TYPE COUNTRY AS ENUM (
    'US', 'CA'
    );

CREATE TABLE ${schema}.users
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY,
    email               VARCHAR(255) UNIQUE NOT NULL,
    password            VARCHAR(255)        NOT NULL,
    role                ROLE                NOT NULL,
    customer_profile_id BIGINT,
    merchant_profile_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.customer_profile
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    address_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.merchant_profile
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    address_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.restaurant
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY,
    name                 VARCHAR(255) NOT NULL,
    description          TEXT,
    pickup_available     BOOLEAN,
    pickup_eta_minutes   INT,
    delivery_available   BOOLEAN      NOT NULL,
    delivery_eta_minutes INT,
    delivery_fee         NUMERIC(6, 2),
    logo_image_url       VARCHAR(255),
    hero_image_url       VARCHAR(255),
    address_id           BIGINT       NOT NULL,
    merchant_profile_id  BIGINT       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.address
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY,
    street1   VARCHAR(255) NOT NULL,
    street2   VARCHAR(255),
    street3   VARCHAR(255),
    city      VARCHAR(255) NOT NULL,
    state     STATE        NOT NULL,
    zipcode   VARCHAR(9)   NOT NULL,
    latitude  NUMERIC(11, 8),
    longitude NUMERIC(11, 8),
    country   COUNTRY      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.operating_hours
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    close_time    TIME WITH TIME ZONE NOT NULL,
    open_time     TIME WITH TIME ZONE NOT NULL,
    day_of_week   ISO_DOW             NOT NULL,
    restaurant_id BIGINT              NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.menu_item
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    name          VARCHAR(50)   NOT NULL,
    description   VARCHAR(255),
    price         NUMERIC(7, 2) NOT NULL,
    image_url     VARCHAR(255),
    is_available  BOOLEAN       NOT NULL DEFAULT TRUE,
    restaurant_id BIGINT        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.orders
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY,
    date_time           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
    tip                 NUMERIC(7, 2),
    is_completed        BOOLEAN                  NOT NULL DEFAULT FALSE,
    payment_method      PAYMENT_METHOD           NOT NULL,
    is_delivery         BOOLEAN                  NOT NULL,
    restaurant_id       BIGINT                   NOT NULL,
    customer_profile_id BIGINT                   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.order_line
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    quantity     INT           NOT NULL,
    price_each   NUMERIC(7, 2) NOT NULL,
    menu_item_id BIGINT        NOT NULL,
    order_id     BIGINT        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.cuisine
(
    id       INT GENERATED ALWAYS AS IDENTITY,
    category VARCHAR(30) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ${schema}.restaurant_cuisine
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    restaurant_id BIGINT   NOT NULL,
    cuisine_id    INT      NOT NULL,
    -- priority/order in which restaurant considers itself to be a particular cuisine
    -- i.e. a Pizza restaurant may chose 1 for Pizza, 2 for Italian, and 3 for Salad
    priority      SMALLINT NOT NULL
);

ALTER TABLE ${schema}.users
    ADD CONSTRAINT fk_customer_profile FOREIGN KEY (customer_profile_id) REFERENCES ${schema}.customer_profile (id),
    ADD CONSTRAINT fk_merchant_profile FOREIGN KEY (merchant_profile_id) REFERENCES ${schema}.merchant_profile (id);

ALTER TABLE ${schema}.customer_profile
    ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES ${schema}.address (id);

ALTER TABLE ${schema}.merchant_profile
    ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES ${schema}.address (id);

ALTER TABLE ${schema}.operating_hours
    ADD CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES ${schema}.restaurant (id);

ALTER TABLE ${schema}.menu_item
    ADD CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES ${schema}.restaurant (id);

ALTER TABLE ${schema}.orders
    ADD CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES ${schema}.restaurant (id),
    ADD CONSTRAINT fk_customer_profile FOREIGN KEY (customer_profile_id) REFERENCES ${schema}.customer_profile (id);

ALTER TABLE ${schema}.order_line
    ADD CONSTRAINT fk_menu_item FOREIGN KEY (menu_item_id) REFERENCES ${schema}.menu_item (id),
    ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES ${schema}.orders (id);

ALTER TABLE ${schema}.restaurant_cuisine
    ADD CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES ${schema}.restaurant (id),
    ADD CONSTRAINT fk_cuisine FOREIGN KEY (cuisine_id) REFERENCES ${schema}.cuisine (id);

CREATE CAST (character varying AS role) WITH INOUT AS ASSIGNMENT;
CREATE CAST (character varying AS state) WITH INOUT AS ASSIGNMENT;
CREATE CAST (character varying AS country) WITH INOUT AS ASSIGNMENT;
