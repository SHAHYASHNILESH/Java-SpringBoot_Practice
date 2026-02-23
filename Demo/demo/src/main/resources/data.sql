CREATE TABLE product (product_id INT PRIMARY KEY,product_name VARCHAR(255),product_category VARCHAR(255),product_price INT);

INSERT INTO product VALUES (1,'Google Pixel 9','Mobile Phones',1200);
INSERT INTO product VALUES (2,'iPad Mini','Tablets',800);
INSERT INTO product VALUES (3,'Canon 1500D','Cameras',500);
INSERT INTO product VALUES (4,'LG 4K Ultra HD TV','Televisions',300);
INSERT INTO product VALUES (5,'Goalkeeper Gloves','Sports Accessories',100);

CREATE TABLE product_output (product_id INT PRIMARY KEY,product_name VARCHAR(255),product_category VARCHAR(255),product_price INT);

CREATE TABLE os_product (product_id INT PRIMARY KEY,product_name VARCHAR(255),product_category VARCHAR(255),product_price INT,tax_percent int,sku varchar(100),shipping_rate int);
