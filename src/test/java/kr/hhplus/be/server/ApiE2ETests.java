package kr.hhplus.be.server;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
                .body("data.userId", equalTo(1));
    }

    @Test
    void 잔액_충전_API() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "amount", 10000))
                .when().post("/api/points/charge")
                .then().statusCode(200)
                .body("data.userId", equalTo(1));
    }

    @Test
    void 주문_생성_API_테스트() {
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", 500)
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
                Map.of("productId", 1, "quantity", 2, "unitPoint", 500)
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

    // ===== 장바구니 관련 API 테스트 추가 =====

    @Test
    void 장바구니_아이템_추가_API() {
        // 새로운 아이템 추가
        Map<String, Object> newItem = Map.of(
                "productId", 1001,
                "productName", "Test Product",
                "quantity", 2,
                "price", 50.0
        );

        given()
                .contentType(ContentType.JSON)
                .body(newItem)
                .when()
                .post("/api/cart/1/items")
                .then()
                .statusCode(200)
                // ApiResponse의 data 아래에 있는 cartItems 리스트 길이 확인
                .body("data.cartItems.size()", greaterThan(0))
                .body("data.cartItems[0].productId", equalTo(1001))
                .body("data.cartItems[0].productName", equalTo("Test Product"))
                .body("data.cartItems[0].quantity", equalTo(2))
                .body("data.cartItems[0].price", equalTo(50.0f));
    }

    @Test
    void 장바구니_아이템_수정_API() {
        // 기존 아이템의 수량을 수정하기 위한 데이터
        Map<String, Object> updatedItem = Map.of(
                "productId", 1001,
                "productName", "Test Product",
                "quantity", 5,
                "price", 50.0
        );
        given()
                .contentType(ContentType.JSON)
                .body(updatedItem)
                .when()
                .put("/api/cart/1/items")
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
                "price", 75.0
        );
        Map<String, Object> newItem2 = Map.of(
                "productId", 1003,
                "productName", "Product 3",
                "quantity", 1,
                "price", 20.0
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
                Map.of("productId", 1, "quantity", 2, "unitPoint", 500)
        );

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(orderItems)
                .queryParam("userId", 1)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .extract().response();

        int orderId = createResponse.path("id");

        given()
                .when()
                .get("/api/orders/{orderId}", orderId)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("orderNumber", notNullValue());
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
