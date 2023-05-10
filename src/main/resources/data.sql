INSERT INTO products (name, price) VALUES ('apple', 12);
INSERT INTO products (name, price) VALUES ('bread', 25.5);
INSERT INTO products (name, price) VALUES ('eggs', 53);
INSERT INTO products (name, price) VALUES ('milk', 37.8);
INSERT INTO products (name, price) VALUES ('water', 18.9);
INSERT INTO products (name, price) VALUES ('banana', 78);
INSERT INTO products (name, price) VALUES ('cake', 289);
INSERT INTO products (name, price) VALUES ('orange', 69.2);
INSERT INTO products (name, price) VALUES ('juice', 81.2);
INSERT INTO products (name, price) VALUES ('potatoes', 9);
INSERT INTO products (name, price) VALUES ('chicken', 108.2);
INSERT INTO products (name, price) VALUES ('pizza', 206);


INSERT INTO customers(name, surname, email, password)
VALUES ('John', 'Doe', 'doe@gmail.com', '$2a$10$L2.WiHEAoVNZG40vz969pOk7twW2tvxNsps0OrtWUzFsx8IoHP4W.');
INSERT INTO customers(name, surname, email, password)
VALUES ('Tom', 'Red', 'red@gmail.com', '$2a$10$5OKqXv6XwdJbcrSAq1tGAOAEKMwgEr6pR8qnu244BQ.00KA/btU2C');
INSERT INTO customers(name, surname, email, password)
VALUES ('Ann', 'Wales', 'ann@gmail.com', '$2a$10$2a/ynkEuWQCySxeT7.rCYuznJpjkWwpXifeAdLVSZSCsRiYs4a9o.');
INSERT INTO customers(name, surname, email, password)
VALUES ('Harry', 'Potter', 'potter@gmail.com', '$2a$10$ih3idxU6PAzmxbX4aFMeUea4/0TDXomSaXuwAE3sRecjMGpEy8XcC');
INSERT INTO customers(name, surname, email, password)
VALUES ('Lili', 'Boston', 'lili@gmail.com', '$2a$10$tS0b4872siOly9CosUc4Eet5qbkySotq/tK0NvO3uEvPB4Y7O64YG');

INSERT INTO orders (creation_date, status, customer_id, delivery_address, description, price)
VALUES ('2014-04-28T16:00:49.455', 'UNPAID', 1, 'Street 56', 'My first order', 74);

INSERT INTO order_product (product_id, order_id) VALUES (1,1);
INSERT INTO order_product (product_id, order_id) VALUES (3,1);
INSERT INTO order_product (product_id, order_id) VALUES (10,1);