DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS option;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS member;

CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(20),
    image_url VARCHAR(1000)
);

CREATE TABLE member (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    role VARCHAR(30) DEFAULT 'USER' NOT NULL
);

CREATE TABLE wishlist (
    id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    UNIQUE(member_id, product_id)
);

CREATE TABLE option (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    product_id INTEGER,
    FOREIGN KEY (product_id) REFERENCES product(id)
);