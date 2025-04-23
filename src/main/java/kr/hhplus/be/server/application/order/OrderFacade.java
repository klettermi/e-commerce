package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final UserService userService;
    private final InventoryService inventoryService;

    @Transactional
    public Order getOrder(Long orderId) {
        return orderService.getOrderById(orderId);
    }

    /**
     * 주문 전체 플로우: 트랜잭션 안에서 두 서비스를 조합
     */
    @Transactional
    public Order placeOrder(Long userId, List<OrderProduct> orderProductList) {
        // 1) 사용자 찾기
        User user = userService.getUser(userId);

        // 2) 총 포인트 계산
        Money totalPointValue = orderService.calculateTotal(orderProductList);

        // 3) Order 인스턴스 생성(Products 포함)
        Order order = orderService.buildOrder(user, orderProductList, totalPointValue);

        // 4) 재고 검증 및 차감
        inventoryService.checkAndDecreaseStock(order.getOrderProducts());

        // 5) DB 저장
        return orderService.saveOrder(order);
    }



}