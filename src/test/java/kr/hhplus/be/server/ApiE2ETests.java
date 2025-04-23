package kr.hhplus.be.server;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import kr.hhplus.be.server.domain.common.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class ApiE2ETests {

    @BeforeEach
    void setupRestAssuredAndSeed() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void get_products_API() {
        given()
                .when().get("/api/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void get_popular_products_API() {
        given()
                .when().get("/api/products/popular")
                .then().statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void get_user_point_API() {
        given()
                .when().get("/api/points/1")
                .then().statusCode(200)
                .body("data.userId", equalTo(1));
    }

    @Test
    void charge_point_API() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "amount", Money.of(1500)))
                .when().post("/api/points/charge")
                .then().statusCode(200)
                .body("data.userId", equalTo(1));
    }

    @Test
    void create_order() {
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", Money.of(1500))
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
    void payment() {
        // 1. 주문 생성
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", Money.of(500))
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
    void get_coupon() {
        given()
                .queryParam("userId", 1)
                .when().get("/api/coupons")
                .then().statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void issue_coupon() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("userId", 1, "couponId", 3))
                .when().post("/api/coupons/issue")
                .then().statusCode(anyOf(is(200), is(409)));
    }

    @Test
    void point_history() {
        given()
                .when().get("/api/points/1/history")
                .then().statusCode(200);
    }

    @Test
    void get_cart() {
        given()
                .when().get("/api/cart/1")
                .then().statusCode(200);
    }


    @Test
    void add_item_in_cart() {
        Map<String, Object> newItem = Map.of(
                "productId", 1001,
                "productName", "Test Product",
                "quantity", 2,
                "price", Money.of(50000)
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
    void update_cart_item() {
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
                "price", Money.of(50000)
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
    void remove_cart_item() {
        given()
                .when()
                .delete("/api/cart/1/items/1001")
                .then()
                .statusCode(200)
                .body("data.cartItems.size()", equalTo(0));
    }

    @Test
    void clear_cart() {
        // 테스트를 위해 두 개의 다른 아이템 추가
        Map<String, Object> newItem1 = Map.of(
                "productId", 1002,
                "productName", "Product 2",
                "quantity", 3,
                "price", Money.of(750000)
        );
        Map<String, Object> newItem2 = Map.of(
                "productId", 1003,
                "productName", "Product 3",
                "quantity", 1,
                "price", Money.of(200000)
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
    void get_order_detail() {
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", Money.of(500))
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
        Long orderId = (long) orderIdInt;

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
    void get_user() {
        given()
                .when().get("/api/users/1")
                .then().statusCode(200)
                .body("username", notNullValue());
    }

    @Test
    void get_inventory() {
        given()
                .when().get("/api/inventory/1")
                .then().statusCode(200)
                .body("stock", greaterThanOrEqualTo(0));
    }
}