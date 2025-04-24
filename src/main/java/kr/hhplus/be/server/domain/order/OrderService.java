package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
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

    /**
     * 주문 총 포인트 계산
     */
    public Money calculateTotal(List<OrderProduct> items) {
        return items.stream()
                .map(i -> i.getUnitPoint().multiply(i.getQuantity()))
                .reduce(Money.ZERO, Money::add);
    }

    /**
     * Order 엔티티 생성
     */
    public Order buildOrder(User user, List<OrderProduct> items, Money totalPoint) {
        Order order = new Order(user.getId(), totalPoint, OrderStatus.CREATED);
        items.forEach(r ->
                order.addOrderProduct(
                        OrderProduct.builder()
                                .productId(r.getProductId())
                                .quantity(r.getQuantity())
                                .unitPoint(r.getUnitPoint())
                                .build()
                )
        );
        return order;
    }

    /**
     * 실제 DB에 저장
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }



}