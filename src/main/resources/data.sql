INSERT INTO app_user (name, email, password) VALUES
('John Doe', 'johndoe1@example.com', 'password123'),
('Jane Smith', 'janesmith2@example.com', 'password123'),
('Alice Johnson', 'alicejohnson3@example.com', 'password123'),
('Bob Brown', 'bobbrown4@example.com', 'password123'),
('Charlie Davis', 'charliedavis5@example.com', 'password123');

INSERT INTO user_roles (user_id, roles) VALUES
(1, 'RIDER'),
(2, 'DRIVER'),
(3, 'RIDER'),
(4, 'DRIVER'),
(5, 'RIDER');


INSERT INTO rider (user_id, rating) VALUES (1, 4.9);

INSERT INTO driver (user_id, rating, available, current_location) VALUES
(2, 4.7, true, ST_GeomFromText('POINT(77.1025 28.7041)', 4326)),
(3, 4.2, true, ST_GeomFromText('POINT(77.2167 28.6667)', 4326)),
(4, 4.4, true, ST_GeomFromText('POINT(77.2273 28.6353)', 4326)),
(5, 4.8, true, ST_GeomFromText('POINT(77.2500 28.5500)', 4326));

INSERT INTO wallet(user_id, balance) VALUES
(1, 100),
(2, 500);
