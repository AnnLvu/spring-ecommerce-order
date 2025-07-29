-- Insert products
INSERT INTO products (name, description, price, image_url, quantity) VALUES
                                                                         ('LaptopPro15', 'High-performance laptop with 15-inch display', 1499.99, 'https://example.com/images/laptop_pro_15.jpg', 10),
                                                                         ('WirelessMouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 'https://example.com/images/wireless_mouse.jpg', 50),
                                                                         ('MechanicalKeyboard', 'RGB mechanical keyboard with blue switches', 89.49, 'https://example.com/images/mechanical_keyboard.jpg', 25),
                                                                         ('NoiseCancellingHeadphones', 'Over-ear headphones with noise cancellation', 199.99, 'https://example.com/images/headphones.jpg', 15),
                                                                         ('USB-CHub', '6-in-1 USB-C hub for Mac and Windows', 45.00, 'https://example.com/images/usb_c_hub.jpg', 40);

-- Insert users
INSERT INTO users (email, password, name, role) VALUES
                                                    ('alice@example.com', 'password1', 'Alice', 'USER'),
                                                    ('bob@example.com', 'password2', 'Bob', 'ADMIN');

-- Insert cart for Alice
INSERT INTO cart (user_id) VALUES
    (1);

-- Insert products into Alice's cart
INSERT INTO cart_products (quantity, product_id, cart_id) VALUES
                                                              (1, 1, 1),  -- LaptopPro15
                                                              (2, 2, 1);  -- WirelessMouse

-- Insert cart statistics (ADD actions by Alice and Bob)
INSERT INTO cart_statistics (action, user_id, product_id) VALUES
                                                              ('ADD', 1, 1),
                                                              ('ADD', 1, 2),
                                                              ('ADD', 2, 3),
                                                              ('ADD', 2, 1),
                                                              ('REMOVE', 1, 2);
