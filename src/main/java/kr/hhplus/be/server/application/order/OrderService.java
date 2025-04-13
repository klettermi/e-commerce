package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import kr.hhplus.be.server.domain.inventory.InventoryChecker;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryChecker inventoryChecker;

    public Order placeOrder(User user, String orderNumber, List<OrderProductRequest> orderRequest) throws InvalidStateException {
        // 주문 항목 리스트 생성
        List<OrderProduct> orderProducts = orderRequest.stream()
                .map(req -> OrderProduct.builder()
                        .productId(req.productId())
                        .quantity(req.quantity())
                        .unitPoint(BigDecimal.valueOf(req.unitPoiont()))
                        .build())
                .toList();

        // 총 결제 포인트 계산
        BigDecimal totalPointValue = orderProducts.stream()
                .map(op -> op.getUnitPoint().multiply(BigDecimal.valueOf(op.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Money totalPoint = new Money(totalPointValue);

        // 주문 생성
        Order order = new Order(user, orderNumber, totalPoint, OrderStatus.CREATED);

        // 주문 항목 추가
        orderProducts.forEach(order::addOrderProduct);

        // 재고 체크
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            if (!inventoryChecker.hasSufficientStock(orderProduct.getProductId(), orderProduct.getQuantity())) {
                throw new InvalidStateException("재고 부족: productId=" + orderProduct.getProductId());
            }
        }

        order = orderRepository.save(order);

        // 재고 차감
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            inventoryChecker.decreaseStock(orderProduct.getProductId(), orderProduct.getQuantity());
        }

        // 주문 상태 변경
        order.markAsPaid();

        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }
}
