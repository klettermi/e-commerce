-- V1__Initial_Schema_No_FK.sql

-- 1. 사용자 (users) 테이블
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 사용자 포인트 (user_points) 테이블
CREATE TABLE user_points (
                             id BIGINT PRIMARY KEY,  -- 사용자와 1:1 관계인 경우 사용자 ID를 그대로 사용하거나 별도의 pk를 둘 수 있습니다.
                             point_balance DECIMAL(19,2) NOT NULL,
                             user_id BIGINT NOT NULL
);

-- 3. 카테고리 (categories) 테이블
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
);

-- 4. 아이템 (items) 테이블
CREATE TABLE items (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(1024),
                       sale_status VARCHAR(50) NOT NULL, -- 예: 'ON_SALE', 'SOLD_OUT'
                       base_price DECIMAL(19,2) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       category_id BIGINT
);

-- 5. 옵션 (options) 테이블
CREATE TABLE options (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         additional_cost DECIMAL(19,2) NOT NULL
);

-- 6. 상품 (products) 테이블
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          item_id BIGINT NOT NULL,
                          option_id BIGINT
);

-- 7. 재고 (inventories) 테이블
CREATE TABLE inventories (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             product_id BIGINT NOT NULL,
                             quantity INT NOT NULL
);

-- 8. 주문 (orders) 테이블
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        order_number VARCHAR(255) NOT NULL,
                        total_point DECIMAL(19,2) NOT NULL,
                        status VARCHAR(50) NOT NULL, -- 예: 'CREATED', 'PAID'
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 9. 주문 상품 (order_products) 테이블
CREATE TABLE order_products (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_id BIGINT NOT NULL,
                                product_id BIGINT NOT NULL,
                                quantity INT NOT NULL,
                                unit_point DECIMAL(19,2) NOT NULL

);

-- 10. 장바구니 (carts) 테이블
CREATE TABLE carts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL
);

-- 11. 장바구니 아이템 (cart_items) 테이블
CREATE TABLE cart_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            product_name VARCHAR(255),
                            quantity INT NOT NULL,
                            price DECIMAL(19,2) NOT NULL
);

-- 12. 결제 (payments) 테이블
CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id BIGINT UNIQUE NOT NULL,
                          payment_amount DECIMAL(19,2) NOT NULL
);

-- 13. 포인트 이력 (point_histories) 테이블
CREATE TABLE point_histories (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 type VARCHAR(50) NOT NULL,   -- 예: 'CHARGE', 'USE'
                                 amount DECIMAL(19,2) NOT NULL,
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
