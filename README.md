# 전자상거래 프로젝트

이 프로젝트는 전자상거래 플랫폼의 기본 구조와 기능을 설계하기 위해 다음 다이어그램을 포함하고 있습니다.  

---

## 프로젝트 개요

본 프로젝트는 온라인 상점을 구축하기 위한 전반적인 요구사항 분석과 설계를 다룹니다.  
주요 목표:

- **도메인 분석**: 시스템 내 주요 도메인 및 관계 파악  
- **사용자 시나리오**: 사용자 흐름 및 요구사항 도출  
- **상태/시퀀스 다이어그램**: 시스템 동작 흐름 설계  
- **이벤트스토밍**: 도메인 이벤트 중심 분석  
- **클래스 다이어그램**: 객체 지향 설계 관점 클래스 관계 정의  

---

## 다이어그램

### 1. 도메인 분석

![도메인 분석](https://github.com/user-attachments/assets/253dcaf0-ea53-4856-93b0-f83f3be6f2a8)

### 2. 사용자 시나리오

![사용자 시나리오](https://github.com/user-attachments/assets/87800c19-b914-4f89-a2f7-3599c818b4ad)

### 3. 상태 다이어그램

![상태 다이어그램](https://github.com/user-attachments/assets/eddaf1ac-c122-4d15-af39-b411d651f53a)

### 4. 시퀀스 다이어그램

![시퀀스 다이어그램](https://github.com/user-attachments/assets/2a5490e1-bd62-4c79-86f0-ef5f9206542a)

### 5. 이벤트스토밍

![이벤트스토밍](https://github.com/user-attachments/assets/21453dcd-e509-4af9-b38b-790e07afba73)

### 6. 클래스 다이어그램

![클래스 다이어그램](https://github.com/user-attachments/assets/11019913-8801-4e9e-8e78-511ba7d41b7a)

---

## 서비스 SQL 성능 분석 및 최적화 보고서

### 1. 개요  
이 보고서는 주요 SQL 쿼리에 대한 실제 응답시간 데이터를 토대로 병목 구간을 식별하고, 인덱스 적용 전·후 및 p(90)/p(95) 지표를 분석하여 최적화 방안을 제시합니다.

### 2. 측정 환경  
- **DB**: MySQL 8.0, InnoDB  
- **데이터 볼륨**: Users 100K, Products 50K, Orders 200K 등  
- **부하 도구**: p6spy 로그 + k6 HTTP 부하 테스트  
- **측정 지표**: 단일 쿼리 응답시간, p(90)/p(95), 최대(ms)  

### 3. 인덱스 적용 전후 성능 비교  

| SQL 구문                                                        | 적용 전 (ms) | 적용 후 (ms) |
|---------------------------------------------------------------|------------:|-----------:|
| `SELECT count(*) FROM users`                                  |          14 |          5 |
| `INSERT INTO users (...) VALUES (...)`                        |           7 |          4 |
| `INSERT INTO user_points (...) VALUES (...)`                  |           8 |          6 |
| `SELECT count(*) FROM products`                               |          10 |          2 |
| `INSERT INTO categories (...) VALUES (...)`                   |           4 |          2 |
| `INSERT INTO items (...) VALUES (...)`                        |           4 |          1 |
| `INSERT INTO options (...) VALUES (...)`                      |           5 |          2 |
| `INSERT INTO products (...) VALUES (...)`                     |           2 |          1 |
| `SELECT … FROM products`                                      |           2 |          2 |
| `SELECT … FROM inventories WHERE product_id = ?`              |           4 |          4 |
| `INSERT INTO inventories (...) VALUES (...)`                  |           2 |          1 |
| `SELECT count(*) FROM orders`                                 |           4 |          1 |
| `INSERT INTO orders (...) VALUES (...)`                       |           2 |          1 |
| `INSERT INTO order_products (...) VALUES (...)`               |           4 |          2 |
| `SELECT count(*) FROM carts`                                  |           3 |          1 |
| `INSERT INTO carts (...) VALUES (...)`                        |           2 |          1 |
| `INSERT INTO cart_items (...) VALUES (...)`                   |           3 |          1 |

> **핵심 관찰**  
> - 집계(`COUNT(*)`) 쿼리: 인덱스 추가로 60–80% 개선  
> - 단건 조회: 이미 인덱스 존재해 변화 없음  
> - DML: 인덱스 쓰기 비용 미미, 소폭 개선  

### 4. 부하 테스트 쿼리 지표 (k6 결과)

| 쿼리 이름               | 평균(ms) | p(90)(ms) | p(95)(ms) | 최대(ms) |
|-----------------------|---------:|---------:|---------:|--------:|
| select_count_users    |    13.41 |       21 |    28.55 |      33 |
| select_count_products |     3.29 |        7 |       8  |      10 |
| select_inventories    |     2.02 |        3 |       4  |      16 |
| select_products       |     2.33 |      3.1 |       6  |      18 |
| select_count_orders   |     1.10 |        2 |       2  |      13 |
| select_count_carts    |     0.86 |        1 |       2  |       7 |
| insert_users          |     3.69 |      6.1 |    9.55  |      15 |
| insert_user_points    |     2.46 |        4 |       6  |       7 |
| insert_categories     |     2.68 |        5 |       6  |       8 |
| insert_items          |     2.12 |        3 |       5  |       7 |
| insert_options        |     2.09 |        4 |       5  |      17 |
| insert_products       |     2.97 |        5 |       6  |      16 |
| insert_inventories    |     3.02 |        5 |    7.55  |      15 |
| insert_orders         |     2.36 |        4 |       6  |      14 |
| insert_order_products |     2.06 |        3 |    4.55  |      10 |
| insert_carts          |     1.69 |        3 |       4  |       8 |
| insert_cart_items     |     1.49 |        2 |       4  |       6 |

> **핵심 관찰**  
> - `select_count_users` p(95)=28ms → 추가 복합 인덱스 고려  
> - 최대치 변동성 큰 쿼리 → 캐싱·키셋 페이징 적용  

### 5. 최적화 권고 사항

1. **추가 인덱스 도입**  
   - `users(status, created_at)` 복합 인덱스  
   - `point_history(user_id, created_at)` 복합 인덱스  

2. **페이징 전략 개선**  
   - OFFSET → **Keyset Pagination** (`WHERE id > :lastId`)

3. **결과 캐싱**  
   - 인기 상품·카테고리별 목록 Redis 캐싱 (TTL 1–5분)

4. **쿼리 리팩토링**  
   - `SELECT count(*)` → 정기 집계 테이블 사용  
   - 불필요 `SELECT *` 제거

5. **운영 모니터링**  
   - Slow Query Log + pt-query-digest 자동 수집  
   - APM 알림 임계치(p(95) 기준) 설정

### 6. 결론  
- 인덱스 적용으로 주요 쿼리 60–80% 개선  
- 캐싱·키셋 페이징 도입 시 일관된 성능 확보  
- 제안안 적용 후 운영환경 재측정 필요


