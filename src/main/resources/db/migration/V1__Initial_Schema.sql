-- V1__Initial_Schema.sql

-- 1. 사용자 (users) 테이블
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL DEFAULT '',  -- username 컬럼 추가 (기본값: 빈 문자열)
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 사용자 포인트 (user_points) 테이블
CREATE TABLE user_points (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             point_balance DECIMAL(19,2) NOT NULL,
                             user_id BIGINT NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. 카테고리 (categories) 테이블
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description VARCHAR(255),
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 아이템 (items) 테이블
CREATE TABLE items (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(1024),
                       sale_status VARCHAR(50) NOT NULL,
                       sale_start_date DATETIME,
                       base_price DECIMAL(19,2) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       category_id BIGINT
);



-- 5. 옵션 (options) 테이블
CREATE TABLE options (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         additional_cost DECIMAL(19,2) NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. 상품 (products) 테이블
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          item_id BIGINT NOT NULL,
                          option_id BIGINT,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. 재고 (inventories) 테이블
CREATE TABLE inventories (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             product_id BIGINT NOT NULL,
                             quantity INT NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 8. 주문 (orders) 테이블
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        order_number VARCHAR(255) NOT NULL,
                        total_point DECIMAL(19,2) NOT NULL,
                        status VARCHAR(50) NOT NULL,  -- 예: 'CREATED', 'PAID'
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 9. 주문 상품 (order_products) 테이블
CREATE TABLE order_products (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_id BIGINT NOT NULL,
                                product_id BIGINT NOT NULL,
                                quantity INT NOT NULL,
                                unit_point DECIMAL(19,2) NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 10. 장바구니 (carts) 테이블
CREATE TABLE carts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 11. 장바구니 아이템 (cart_items) 테이블
CREATE TABLE cart_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            product_name VARCHAR(255),
                            quantity INT NOT NULL,
                            price DECIMAL(19,2) NOT NULL,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 12. 결제 (payments) 테이블
CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id BIGINT UNIQUE NOT NULL,
                          payment_amount DECIMAL(19,2) NOT NULL,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 13. 포인트 이력 (point_histories) 테이블
CREATE TABLE point_histories (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 type VARCHAR(50) NOT NULL,   -- 예: 'CHARGE', 'USE'
                                 amount DECIMAL(19,2) NOT NULL,
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
