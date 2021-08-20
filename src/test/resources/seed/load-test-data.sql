INSERT INTO address (id, street1, street2, street3, city, state, zipcode, country, latitude, longitude)
VALUES (1, '1139 W Grand Ave', null, null, 'Chicago', 'IL', '60422', 'US', 41.89218657942997, -87.65593535456857),
       (2, '7551 175th St', null, null, 'Tinley Park', 'IL', '60477', 'US', 41.57284878938713, -87.80089818659059),
       (3, '100 W Ontario St', null, null, 'Chicago', 'IL', '60654', 'US', 41.89364806160217, -87.63155681673796),
       (4, '864 N State St', null, null, 'Chicago', 'IL', '60610', 'US', 41.89864725197439, -87.62848337605959),
       (5, '7101W 183rd St', null, null, 'Tinley Park', 'IL', '60477', 'US', 41.55826391251718, -87.79020967441441),
       (6, '6257 S Cottage Grove Ave', null, null, 'Chicago', 'IL', '60637', 'US', 41.780773791764865,
        -87.60568093023281);

INSERT INTO customer_profile (id, first_name, last_name, address_id)
VALUES (1, 'John', 'Smith', 1),
       (2, 'Alice', 'Miller', 2);

INSERT INTO merchant_profile (id, first_name, last_name, address_id)
VALUES (1, 'Sam', 'Wilson', null),
       (2, 'Rebecca', 'Black', null);

INSERT INTO users (id, email, password, is_deactivated, role, customer_profile_id, merchant_profile_id)
VALUES (1, 'john_customer@example.com', 'password', FALSE, 'CUSTOMER', 1, null),
       (2, 'alice_customer@example.com', 'password', FALSE, 'CUSTOMER', 2, null),
       (3, 'sam_merchant@example.com', 'password', FALSE, 'MERCHANT', null, 1),
       (4, 'rebecca_merchant@example.com', 'password', FALSE, 'MERCHANT', null, 2);

INSERT INTO restaurant (id, name, description, pickup_available, pickup_eta_minutes, delivery_available,
                        delivery_eta_minutes, delivery_fee, logo_image_url, hero_image_url, is_active, address_id,
                        merchant_profile_id)
VALUES (1, 'Portillo''s Hot Dogs',
        'Chicago-born chain for hot dogs, Italian beef sandwiches & more, plus beer & wine, in retro digs.', true, 30,
        true, 45, 5.0, null, null, true, 3, 2),
       (2, 'Pizano''s Pizza & Pasta', 'Malnati roots that dishes deep-dish & thin-crust pies plus pasta & more.', true,
        20, true, 34, 4.0, null, null, true, 4, 1),
       (3, 'Joy Yee', 'Delicious Pan-Asian food and boba.', true, 20, true, 40, 4.50, null, null, true, 5, 2),
       (4, 'Daley''s Restaurant',
        'Historic diner serving up a variety of homestyle classics & soul food, from waffles to pork chops.', true, 30,
        false, null, null, null, null, false, 6, 2);

INSERT INTO menu_item (id, name, description, price, image_url, is_available, restaurant_id)
VALUES (1, 'Chicago-Style Hot Dog', 'Hot dog with relish, onions, tomatoes, peppers, and mustard', null, TRUE, 3.5, 1),
       (2, 'Italian Beef', 'Famous slow-roasted Italian beef sandwich with carrots, onions, giardiniera peppers', null,
        TRUE, 3.5, 1),
       (3, 'Cheese Pizza', 'Thin crust cheese pizza', null, true, 16.0, 2),
       (4, 'Sausage Pizza', 'Deep dish sausage pizza', null, true, 18.25, 2),
       (5, 'Chicken & Pork Dumplings', null, null, true, 7.95, 3),
       (6, 'Szechuan Beef', 'Thinly sliced beef, stir-fried with spicy chilis and peppercorns', null, true, 14.95, 3),
       (7, 'Fried Chicken and Belgian Waffles', 'A huge Belgian waffle and 4 pieces of fried chicken made to order.',
        null, TRUE, 14.75, 4),
       (8, 'Roasted Turkey Sandwich', 'Roasted turkey served hot with lettuce, tomato and mayo. Comes with fries.',
        null, FALSE, 9.25, 4);

INSERT INTO operating_hours (id, open_time, close_time, day_of_week, restaurant_id)
VALUES (1, TIME '08:00:00', TIME '21:00:00', 'MONDAY', 1),
       (2, TIME '11:00:00', TIME '23:00:00', 'TUESDAY', 2),
       (3, TIME '11:00:00', TIME '21:00:00', 'WEDNESDAY', 3),
       (4, TIME '07:00:00', TIME '20:00:00', 'THURSDAY', 4);

INSERT INTO orders (id, date_time, tip, is_completed, is_delivery, payment_method, customer_profile_id, restaurant_id)
VALUES (1, TIMESTAMP '2021-01-17 12:31:47.0', 4.0, true, true, 'CARD', 2, 1),
       (2, TIMESTAMP '2021-06-15 18:14:10.0', 5.0, true, true, 'PAYPAL', 1, 2),
       (3, TIMESTAMP '2021-07-22 17:50:13.0', 0.00, true, false, 'CARD', 2, 4),
       (4, CURRENT_TIME, 3.5, true, false, 'APPLE_PAY', 2, 3);


INSERT INTO order_line (id, quantity, price_each, menu_item_id, order_id)
VALUES (1, 2, 3.5, 1, 1),
       (2, 1, 3.5, 2, 1),
       (3, 1, 16.0, 3, 2),
       (4, 1, 18.25, 4, 2),
       (5, 2, 9.25, 8, 3),
       (6, 2, 14.95, 6, 4);
