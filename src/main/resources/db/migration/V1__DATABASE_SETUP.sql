CREATE TYPE ISO_DOW AS ENUM (
    'MONDAY' , 'TUESDAY' , 'WEDNESDAY' , 'THURSDAY' , 'FRIDAY' , 'SATURDAY' , 'SUNDAY'
    );

CREATE TYPE PAYMENT_METHOD AS ENUM (
    'CARD', 'APPLE_PAY' , 'PAYPAL'
    );

CREATE TYPE ROLE AS ENUM (
    'ADMIN', 'CUSTOMER', 'MERCHANT'
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
    'DC', 'ON', 'QC', 'BC', 'AB'
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
    is_active           BOOLEAN             NOT NULL DEFAULT FALSE,
    customer_profile_id BIGINT,
    merchant_profile_id BIGINT,
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


CREATE TABLE ${schema}.customer_profile
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    address_id BIGINT REFERENCES ${schema}.address (id)
);

CREATE TABLE ${schema}.merchant_profile
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    address_id BIGINT REFERENCES ${schema}.address (id)
);

CREATE TABLE ${schema}.restaurant
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    description          TEXT,
    pickup_available     BOOLEAN,
    pickup_eta_minutes   INT,
    delivery_available   BOOLEAN      NOT NULL,
    delivery_eta_minutes INT,
    delivery_fee         BIGINT, -- cents
    logo_image_url       VARCHAR(255),
    hero_image_url       VARCHAR(255),
    is_active            BOOLEAN      NOT NULL DEFAULT TRUE,
    address_id           BIGINT       NOT NULL REFERENCES ${schema}.address (id),
    merchant_profile_id  BIGINT       NOT NULL REFERENCES ${schema}.merchant_profile (id)
);


CREATE TABLE ${schema}.operating_hours
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    close_time    TIME WITH TIME ZONE NOT NULL,
    open_time     TIME WITH TIME ZONE NOT NULL,
    day_of_week   ISO_DOW             NOT NULL,
    restaurant_id BIGINT              NOT NULL REFERENCES ${schema}.restaurant (id)
);

CREATE TABLE ${schema}.menu_item
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    description   VARCHAR(255),
    price         BIGINT      NOT NULL, --cents
    image_url     VARCHAR(255),
    is_available  BOOLEAN     NOT NULL DEFAULT TRUE,
    restaurant_id BIGINT      NOT NULL REFERENCES ${schema}.restaurant (id)
);

CREATE TABLE ${schema}.orders
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    date_time           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
    tip                 BIGINT, -- cents
    is_completed        BOOLEAN                  NOT NULL DEFAULT FALSE,
    payment_method      PAYMENT_METHOD           NOT NULL,
    is_delivery         BOOLEAN                  NOT NULL,
    delivery_fee        BIGINT, -- cents
    restaurant_id       BIGINT                   NOT NULL REFERENCES ${schema}.restaurant (id),
    customer_profile_id BIGINT                   NOT NULL REFERENCES ${schema}.customer_profile (id)
);

CREATE TABLE ${schema}.order_line
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    quantity     INT    NOT NULL,
    price_each   BIGINT NOT NULL, --cents
    menu_item_id BIGINT NOT NULL REFERENCES ${schema}.menu_item (id),
    order_id     BIGINT NOT NULL REFERENCES ${schema}.orders (id)
);

CREATE TABLE ${schema}.cuisine
(
    id       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE ${schema}.restaurant_cuisine
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT   NOT NULL REFERENCES ${schema}.restaurant (id),
    cuisine_id    INT      NOT NULL REFERENCES ${Schema}.cuisine (id),
    -- priority/order in which restaurant considers itself to be a particular cuisine
    -- i.e. a Pizza restaurant may chose 1 for Pizza, 2 for Italian, and 3 for Salad
    priority      SMALLINT NOT NULL
);

CREATE CAST (character varying AS role) WITH INOUT AS ASSIGNMENT;
CREATE CAST (character varying AS state) WITH INOUT AS ASSIGNMENT;
CREATE CAST (character varying AS country) WITH INOUT AS ASSIGNMENT;
