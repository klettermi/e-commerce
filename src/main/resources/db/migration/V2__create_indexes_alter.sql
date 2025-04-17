-- V2__create_indexes_alter.sql
-- 테이블별 자주 사용되는 컬럼에 인덱스를 미리 생성하는 스크립트 (ALTER TABLE 구문 사용)

-- orders 테이블: user_id 컬럼에 인덱스 추가
ALTER TABLE orders ADD INDEX idx_orders_user_id (user_id);

-- order_products 테이블: order_id 컬럼에 인덱스 추가
ALTER TABLE order_products ADD INDEX idx_order_products_order_id (order_id);

-- items 테이블: category_id 컬럼에 인덱스 추가
ALTER TABLE items ADD INDEX idx_items_category_id (category_id);

-- cart_items 테이블: product_id 컬럼에 인덱스 추가 (검색 빈도가 높을 경우)
ALTER TABLE cart_items ADD INDEX idx_cart_items_product_id (product_id);

-- carts 테이블: user_id 컬럼에 인덱스 추가 (사용 빈도에 따라)
ALTER TABLE carts ADD INDEX idx_carts_user_id (user_id);
