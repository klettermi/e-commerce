package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
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
    private final InventoryRepository inventoryRepository;

    @Transactional
    public Order placeOrder(User user, String orderNumber, List<OrderProduct> orderRequest) {
        Money totalPointValue = orderRequest.stream()
                .map(req -> req.getUnitPoint().multiply(req.getQuantity()))
                .reduce(Money.ZERO, Money::add);

        Order order = new Order(user.getId(), orderNumber, totalPointValue, OrderStatus.CREATED);
        orderRequest.forEach(r ->
                order.addOrderProduct(OrderProduct.builder()
                        .productId(r.getProductId())
                        .quantity(r.getQuantity())
                        .unitPoint(r.getUnitPoint())
                        .build())
        );

        for (OrderProduct p : order.getOrderProducts()) {
            Inventory inv = inventoryRepository
                    .findByProductIdForUpdate(p.getProductId())  // @Lock(PESSIMISTIC_WRITE)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Inventory not found: productId=" + p.getProductId()));

            if (inv.getQuantity() < p.getQuantity()) {
                throw new InvalidStateException("재고 부족: productId=" + p.getProductId());
            }
            inv.decreaseStock(p.getQuantity());
        }

        order.markAsPaid();
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}