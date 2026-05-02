-- Sample data for local development.
-- Loaded by `make fixtures` against a running stack (table created by Hibernate
-- on first app boot). Resets the table so IDs are predictable each time.

TRUNCATE TABLE product RESTART IDENTITY;

INSERT INTO product (name, description, price) VALUES
    ('Laptop',    'Dell XPS 13 (2026)',     1500.00),
    ('Mouse',     'Logitech MX Master 3S',  99.99),
    ('Keyboard',  'Keychron K2 v2',         129.00),
    ('Monitor',   'LG UltraFine 4K 27"',    699.00),
    ('USB-C Hub', 'Anker 7-in-1 PD',        59.99);
