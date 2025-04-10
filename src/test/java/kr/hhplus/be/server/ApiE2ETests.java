package kr.hhplus.be.server;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class ApiE2ETests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
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
                .body("userId", equalTo(1));
    }

    @Test
    void 잔액_충전_API() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "amount", 10000))
                .when().post("/api/points/charge")
                .then().statusCode(200)
                .body("userId", equalTo(1));
    }

    @Test
    void 주문_생성_API() {
        var orderItems = List.of(
                Map.of("productId", 10, "amount", 1),
                Map.of("productId", 12, "amount", 2)
        );

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "orderItems", orderItems))
                .when().post("/api/orders")
                .then().statusCode(200)
                .body("status", equalTo("PENDING"));
    }

    @Test
    void 주문_결제_API() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "couponId", 3))
                .when().post("/api/orders/101/pay")
                .then().statusCode(200)
                .body("status", equalTo("PAID"));
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
    void 주문_상세_API() {
        given()
                .when().get("/api/orders/1")
                .then().statusCode(200);
    }

    @Test
    void 결제_상세_API() {
        given()
                .when().get("/api/payments/1")
                .then().statusCode(200);
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
