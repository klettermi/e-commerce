package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.InventoryChecker;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.EntityNotFoundException;
import static kr.hhplus.be.server.domain.common.exception.DomainException.InvalidStateException;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryChecker inventoryChecker;

    public Order placeOrder(User user, String orderNumber, List<OrderProduct> orderProductList) throws InvalidStateException {
        // 총 결제 포인트 계산
        Money totalPointValue = orderProductList.stream()
                .map(op -> op.getUnitPoint().multiply(op.getQuantity()))
                .reduce(Money.ZERO, Money::add);

        // 주문 생성
        Order order = new Order(user.getId(), orderNumber, totalPointValue, OrderStatus.CREATED);

        // 주문 항목 추가
        orderProductList.forEach(order::addOrderProduct);

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

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}
