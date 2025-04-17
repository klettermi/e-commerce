import sql    from 'k6/x/sql';
import driver from 'k6/x/sql/driver/mysql';
import { sleep, group } from 'k6';
import { Trend } from 'k6/metrics';

// 각 쿼리별 응답시간을 기록할 Trend 메트릭 선언
const tSelectUsers        = new Trend('select_count_users');
const tInsertUsers        = new Trend('insert_users');
const tInsertUserPoints   = new Trend('insert_user_points');
const tSelectProducts     = new Trend('select_count_products');
const tInsertCategories   = new Trend('insert_categories');
const tInsertItems        = new Trend('insert_items');
const tInsertOptions      = new Trend('insert_options');
const tInsertProducts     = new Trend('insert_products');
const tSelectAllProducts  = new Trend('select_products');
const tSelectInventories  = new Trend('select_inventories');
const tInsertInventories  = new Trend('insert_inventories');
const tSelectOrders       = new Trend('select_count_orders');
const tInsertOrders       = new Trend('insert_orders');
const tInsertOrderProducts= new Trend('insert_order_products');
const tSelectCarts        = new Trend('select_count_carts');
const tInsertCarts        = new Trend('insert_carts');
const tInsertCartItems    = new Trend('insert_cart_items');

// MySQL 접속: 사용자, 비밀번호, 호스트, 포트, DB명
const db = sql.open(
    driver,
    'root:root@tcp(127.0.0.1:3306)/hhplus?parseTime=true'
);

export let options = {
    vus: 10,
    duration: '30s',
    thresholds: {
        // 읽기 쿼리: 90/95/99 백분위 체크
        'select_count_users':      ['p(90)<40',  'p(95)<50',  'p(99)<80'],
        'select_count_products':   ['p(90)<40',  'p(95)<50',  'p(99)<80'],
        'select_products':         ['p(90)<40',  'p(95)<50',  'p(99)<80'],
        'select_inventories':      ['p(90)<40',  'p(95)<50',  'p(99)<80'],
        'select_count_orders':     ['p(90)<40',  'p(95)<50',  'p(99)<80'],
        'select_count_carts':      ['p(90)<40',  'p(95)<50',  'p(99)<80'],

        // 쓰기 쿼리: 90/95/99 백분위 체크
        'insert_users':            ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_user_points':      ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_categories':       ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_items':            ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_options':          ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_products':         ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_inventories':      ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_orders':           ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_order_products':   ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_carts':            ['p(90)<80',  'p(95)<100', 'p(99)<150'],
        'insert_cart_items':       ['p(90)<80',  'p(95)<100', 'p(99)<150'],
    },
};

export default function () {
    // 1. 사용자 수 조회
    group('SELECT count(*) from users', () => {
        const start = Date.now();
        db.exec(`select count(*) from users u1_0`);
        tSelectUsers.add(Date.now() - start);
    });

    // 2. 사용자 등록 (username에 __VU, __ITER 결합)
    group('INSERT into users', () => {
        const start    = Date.now();
        const username = `loadTestUser_${__VU}_${__ITER}`;
        db.exec(`
            insert into users (created_at, updated_at, username)
            values (NOW(), NOW(), '${username}')
        `);
        tInsertUsers.add(Date.now() - start);
    });

    // 3. 포인트 초기화
    group('INSERT into user_points', () => {
        const start = Date.now();
        db.exec(`
            insert into user_points (created_at, point_balance, updated_at, user_id)
            values (NOW(), 100000, NOW(), LAST_INSERT_ID())
        `);
        tInsertUserPoints.add(Date.now() - start);
    });

    // 4. 상품 수 조회
    group('SELECT count(*) from products', () => {
        const start = Date.now();
        db.exec(`select count(*) from products p1_0`);
        tSelectProducts.add(Date.now() - start);
    });

    // 5. 카테고리 등록
    group('INSERT into categories', () => {
        const start = Date.now();
        db.exec(`
            insert into categories (created_at, description, name, updated_at)
            values (NOW(), null, '일반', NOW())
        `);
        tInsertCategories.add(Date.now() - start);
    });

    // 6. 아이템 등록
    group('INSERT into items', () => {
        const start = Date.now();
        db.exec(`
            insert into items (
                base_price, category_id, created_at,
                description, name, sale_start_date, sale_status, updated_at
            )
            values (
                       100000, 1, NOW(),
                       'AirForce Description', 'AirForce', NOW(), 'ON_SALE', NOW()
                   )
        `);
        tInsertItems.add(Date.now() - start);
    });

    // 7. 옵션 등록
    group('INSERT into options', () => {
        const start = Date.now();
        db.exec(`
            insert into options (additional_cost, created_at, name, updated_at)
            values (1000, NOW(), 'White240', NOW())
        `);
        tInsertOptions.add(Date.now() - start);
    });

    // 8. 상품 등록
    group('INSERT into products', () => {
        const start = Date.now();
        db.exec(`
            insert into products (created_at, item_id, option_id, updated_at)
            values (NOW(), 1, 1, NOW())
        `);
        tInsertProducts.add(Date.now() - start);
    });

    // 9. 모든 상품 조회
    group('SELECT ... from products', () => {
        const start = Date.now();
        db.exec(`select id, created_at, item_id, option_id, updated_at from products`);
        tSelectAllProducts.add(Date.now() - start);
    });

    // 10. 특정 상품 재고 조회
    group('SELECT ... from inventories', () => {
        const start = Date.now();
        db.exec(`
            select id, created_at, product_id, quantity, updated_at
            from inventories
            where product_id=1
        `);
        tSelectInventories.add(Date.now() - start);
    });

    // 11. 재고 추가
    group('INSERT into inventories', () => {
        const start = Date.now();
        db.exec(`
            insert into inventories (created_at, product_id, quantity, updated_at)
            values (NOW(), 1, 10000, NOW())
        `);
        tInsertInventories.add(Date.now() - start);
    });

    // 12. 주문 수 조회
    group('SELECT count(*) from orders', () => {
        const start = Date.now();
        db.exec(`select count(*) from orders o1_0`);
        tSelectOrders.add(Date.now() - start);
    });

    // 13. 주문 등록
    group('INSERT into orders', () => {
        const start = Date.now();
        db.exec(`
            insert into orders (
                created_at, order_number, status, total_point, updated_at, user_id
            )
            values (NOW(), UUID(), 'CREATED', 200000, NOW(), 1)
        `);
        tInsertOrders.add(Date.now() - start);
    });

    // 14. 주문상품 등록
    group('INSERT into order_products', () => {
        const start = Date.now();
        db.exec(`
            insert into order_products (
                created_at, order_id, product_id, quantity, unit_point, updated_at
            )
            values (NOW(), LAST_INSERT_ID(), 1, 2, 100000, NOW())
        `);
        tInsertOrderProducts.add(Date.now() - start);
    });

    // 15. 장바구니 수 조회
    group('SELECT count(*) from carts', () => {
        const start = Date.now();
        db.exec(`select count(*) from carts c1_0`);
        tSelectCarts.add(Date.now() - start);
    });

    // 16. 장바구니 등록
    group('INSERT into carts', () => {
        const start = Date.now();
        db.exec(`
            insert into carts (created_at, updated_at, user_id)
            values (NOW(), NOW(), 1)
        `);
        tInsertCarts.add(Date.now() - start);
    });

    // 17. 장바구니 아이템 등록
    group('INSERT into cart_items', () => {
        const start = Date.now();
        db.exec(`
            insert into cart_items (
                cart_id, created_at, price, product_id, product_name, quantity, updated_at
            )
            values (1, NOW(), 10000, 1, 'AirForce', 3, NOW())
        `);
        tInsertCartItems.add(Date.now() - start);
    });

    // 유저 간 간격
    sleep(1);
}
