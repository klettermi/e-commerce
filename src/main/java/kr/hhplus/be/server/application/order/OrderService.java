package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.common.exception.BusinessExceptionHandler;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.common.exception.DomainException;
import kr.hhplus.be.server.domain.common.exception.ErrorCodes;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.inventory.InventoryChecker;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.order.OrderProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.common.exception.DomainException.*;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryChecker inventoryChecker;

    @Retryable(
            value = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public Order placeOrder(User user, String orderNumber, List<OrderProductRequest> orderRequest) throws InvalidStateException {
        // 주문 항목 리스트 생성
        List<OrderProduct> orderProducts = orderRequest.stream()
                .map(req -> OrderProduct.builder()
                        .productId(req.productId())
                        .quantity(req.quantity())
                        .unitPoint(req.unitPoint())
                        .build())
                .toList();

        // 총 결제 포인트 계산
        Money totalPointValue = orderProducts.stream()
                .map(op -> op.getUnitPoint().multiply(op.getQuantity()))
                .reduce(Money.ZERO, Money::add);

        // 주문 생성
        Order order = new Order(user, orderNumber, totalPointValue, OrderStatus.CREATED);

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
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

    @Recover
    public Order recover(ObjectOptimisticLockingFailureException retryEx, Long orderId) {
        throw new BusinessExceptionHandler(
                ErrorCodes.CONCURRENCY_COMFLICT_NOT_RESOLVED,
                "동시성 충돌로 주문 실패 (orderId=" + orderId + ")",
                retryEx.getStackTrace()
        );
    }
}
