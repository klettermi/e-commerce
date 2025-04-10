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
    void 주문_생성_API_테스트() {
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", 1, "quantity", 2, "unitPoint", 500)
        );

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(orderItems)
                        .queryParam("userId", 1)
                        .when()
                        .post("/api/orders")
                        .then()
                        .log().all()
                        .statusCode(201)
                        .body("orderNumber", notNullValue())
                        .body("status", anyOf(is("CREATED"), is("PAID")))
                        .extract()
                        .response();

        int orderId = response.path("id");
        System.out.println("생성된 주문 ID: " + orderId);
    }

    @Test
    void 결제_API_테스트() {
        // 1. 주문 생성
        // 테스트용 주문 항목 데이터: DataLoader에서 시딩된 상품(productId 1)을 사용하도록 합니다.
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
                .log().all() // 주문 생성 응답 디버깅
                .statusCode(201)
                .body("orderNumber", notNullValue())
                .extract().response();

        int orderId = orderResponse.path("id");
        System.out.println("생성된 주문 ID: " + orderId);

        // 2. 결제 처리
        Response payResponse = given()
                .contentType(ContentType.JSON)
                .queryParam("userId", 1)
                // couponId가 필요하지 않다면 생략, 필요시 .queryParam("couponId", someValue)를 추가
                .when()
                .post("/api/payments/{orderId}/pay", orderId)
                .then()
                .log().all() // 결제 응답 디버깅
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", is("PAID"))
                .extract().response();

        System.out.println("결제 완료된 주문 ID: " + payResponse.path("id"));
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
    void 주문_상세_API_테스트() {
        // 주문 먼저 생성하여 주문 id를 추출 (위 테스트와 유사한 방식)
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
                .log().all()  // 디버깅용 응답 전체 출력
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
                // 재고 조회 API는 "stock"이라는 필드로 재고 수를 반환한다고 가정
                .body("stock", greaterThanOrEqualTo(0));
    }
}
