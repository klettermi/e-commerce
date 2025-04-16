package kr.hhplus.be.server;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import kr.hhplus.be.server.domain.common.Money;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestDataSeeder.class)
@Testcontainers
@TestPropertySource(properties = {
        "spring.profiles.active=test",
        "spring.flyway.enabled=false",
        "spring.flyway.clean-disabled=false",
        "spring.jpa.defer-datasource-initialization=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiE2ETests {

    @LocalServerPort
    private int port;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        if (!mysql.isRunning()) {
            mysql.start();
        }

        String jdbcUrl = mysql.getJdbcUrl();
        String username = mysql.getUsername();
        String password = mysql.getPassword();

        // Flyway 수동 실행 (Spring Context보다 먼저)
        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .load();

        flyway.clean();    // 💣 데이터 초기화
        flyway.migrate();  // 🛠️ 마이그레이션

        // Spring DataSource 설정은 마지막에 등록해야 함
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> username);
        registry.add("spring.datasource.password", () -> password);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }



    @Autowired
    private TestDataSeeder testDataSeeder;


    @BeforeAll
    void init() {
        testDataSeeder.testSeedData();
    }

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }


    @Test
    void 상품_조회_API() {
        given()
                .when().get("/api/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void 인기상품_조회_API() {
        given()
                .when().get("/api/products/popular")
                .then().statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void 잔액_조회_API() {
        given()
                .when().get("/api/points/1")
                .then().statusCode(200)
                .body("data.userId", equalTo(1));
    }

    @Test
    void 잔액_충전_API() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "amount", new Money(BigDecimal.valueOf(1500))))
                .when().post("/api/points/charge")
                .then().statusCode(200)
                .body("data.userId", equalTo(1));
    }

    @Test
    void 주문_생성_API_테스트() {
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", new Money(BigDecimal.valueOf(1500)))
        );


        given()
                .contentType(ContentType.JSON)
                .body(orderItems)
                .queryParam("userId", 1)
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.orderNumber", notNullValue())
                .body("data.status", anyOf(is("CREATED"), is("PAID")))
                .extract()
                .response();
    }

    @Test
    void 결제_API_테스트() {
        // 1. 주문 생성
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", new Money(BigDecimal.valueOf(500)))
        );

        Response orderResponse = given()
                .contentType(ContentType.JSON)
                .body(orderItems)
                .queryParam("userId", 1)
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.orderNumber", notNullValue())
                .extract().response();

        Number orderIdNumber = orderResponse.path("data.id");
        long orderId = orderIdNumber.longValue();

        // 2. 결제 처리
        given()
                .contentType(ContentType.JSON)
                .queryParam("userId", 1)
                .when()
                .post("/api/payments/{orderId}/pay", orderId)
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();
    }

    @Test
    void 쿠폰_조회_API() {
        given()
                .queryParam("userId", 1)
                .when().get("/api/coupons")
                .then().statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void 쿠폰_발급_API() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "couponId", 3))
                .when().post("/api/coupons/issue")
                .then().statusCode(anyOf(is(200), is(409)));
    }

    @Test
    void 포인트_히스토리_API() {
        given()
                .when().get("/api/points/history/1")
                .then().statusCode(200);
    }

    @Test
    void 장바구니_조회_API() {
        given()
                .when().get("/api/cart/1")
                .then().statusCode(200);
    }


    @Test
    void 장바구니_아이템_추가_API() {
        Map<String, Object> newItem = Map.of(
                "productId", 1001,
                "productName", "Test Product",
                "quantity", 2,
                "price", new Money(BigDecimal.valueOf(50000))
        );

        given()
                .when()
                .delete("/api/cart/1")
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", equalTo(0));

        given()
                .contentType(ContentType.JSON)
                .body(newItem)
                .when()
                .post("/api/cart/1/items")
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", greaterThan(0))
                .body("data.cartItems[0].productId", equalTo(1001))
                .body("data.cartItems[0].productName", equalTo("Test Product"))
                .body("data.cartItems[0].quantity", equalTo(2))
                .body("data.cartItems[0].price.amount", equalTo(50000));

    }

    @Test
    void 장바구니_아이템_수정_API() {
        Long userId = 1L;

        // 1. 장바구니 비우기 (테스트 환경 초기화)
        given()
                .when()
                .delete("/api/cart/{userId}", userId)
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", equalTo(0));

        // 2. 장바구니에 아이템 추가 (예: 수량 2)
        Map<String, Object> newItem = Map.of(
                "productId", 1,
                "productName", "Test Product",
                "quantity", 2,
                "price", new Money(BigDecimal.valueOf(50000))
        );
        given()
                .contentType(ContentType.JSON)
                .body(newItem)
                .when()
                .post("/api/cart/{userId}/items", userId)
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", greaterThan(0));

        // 3. 아이템의 수량을 5로 업데이트
        Map<String, Object> updatedItem = Map.of(
                "productId", 1,
                "quantity", 5
        );
        given()
                .contentType(ContentType.JSON)
                .body(updatedItem)
                .when()
                .put("/api/cart/{userId}/items", userId)
                .then()
                .statusCode(200)
                .body("data.cartItems[0].quantity", equalTo(5));
    }


    @Test
    void 장바구니_아이템_제거_API() {
        given()
                .when()
                .delete("/api/cart/1/items/1001")
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", equalTo(0));
    }

    @Test
    void 장바구니_전체_비우기_API() {
        // 테스트를 위해 두 개의 다른 아이템 추가
        Map<String, Object> newItem1 = Map.of(
                "productId", 1002,
                "productName", "Product 2",
                "quantity", 3,
                "price", new Money(BigDecimal.valueOf(750000))
        );
        Map<String, Object> newItem2 = Map.of(
                "productId", 1003,
                "productName", "Product 3",
                "quantity", 1,
                "price", new Money(BigDecimal.valueOf(200000))
        );
        // 아이템 추가
        given()
                .contentType(ContentType.JSON)
                .body(newItem1)
                .when()
                .post("/api/cart/1/items")
                .then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(newItem2)
                .when()
                .post("/api/cart/1/items")
                .then()
                .statusCode(200);
        // 장바구니 전체 비우기 호출
        given()
                .when()
                .delete("/api/cart/1")
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", equalTo(0));
    }

    @Test
    void 주문_상세_API_테스트() {
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", new Money(BigDecimal.valueOf(500)))
        );

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(orderItems)
                .queryParam("userId", 1)
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.orderNumber", notNullValue())
                .body("data.status", anyOf(is("CREATED"), is("PAID")))
                .extract().response();

        // 응답의 id 값을 int로 읽음
        int orderIdInt = createResponse.path("data.id");
        Long orderId = Long.valueOf(orderIdInt);

        given()
                .when()
                .get("/api/orders/{orderId}", orderId)
                .then()
                .log().all()
                .statusCode(200)
                // 비교 시에 응답값은 int로 비교하거나, orderId을 intValue()로 변환하여 비교
                .body("data.id", equalTo(orderId.intValue()))
                .body("data.orderNumber", notNullValue());
    }



    @Test
    void 사용자_조회_API() {
        given()
                .when().get("/api/users/1")
                .then().statusCode(200)
                .body("email", notNullValue());
    }

    @Test
    void 재고_조회_API() {
        given()
                .when().get("/api/inventory/1")
                .then().statusCode(200)
                .body("stock", greaterThanOrEqualTo(0));
    }
}
