package kr.hhplus.be.server;

import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.cart.CartJpaRepository;
import kr.hhplus.be.server.infrastructure.category.CategoryJpaRepository;
import kr.hhplus.be.server.infrastructure.inventory.InventoryJpaRepository;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.option.OptionJpaRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.point.UserPointJpaRepository;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.api.category.CategoryRequest;
import kr.hhplus.be.server.interfaces.api.item.ItemRequest;
import kr.hhplus.be.server.interfaces.api.option.OptionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class TestDataSeeder {

    private final UserJpaRepository userJpaRepository;
    private final UserPointJpaRepository userPointJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final OptionJpaRepository optionJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final InventoryJpaRepository inventoryJpaRepository;
    private final CartJpaRepository cartRepository;

    @Transactional  // 트랜잭션 보장
    public void testSeedData() {

        log.info("✅ Test Data Seeder 실행됨");

        // 사용자 및 UserPoint 시딩
        User managedUser;
        if (userJpaRepository.count() == 0) {
            User user = User.builder()
                    .username("defaultUser")
                    .build();
            managedUser = userJpaRepository.saveAndFlush(user);

            UserPoint userPoint = new UserPoint();
            userPoint.setUser(managedUser);
            userPoint.chargePoints(Money.of(100000));
            userPointJpaRepository.save(userPoint);
        } else {
            managedUser = userJpaRepository.findAll().get(0);
        }

        // 상품 시딩
        Product product;
        if (productJpaRepository.count() == 0) {
            CategoryRequest categoryRequest = new CategoryRequest("일반");
            Category category = Category.fromDto(categoryRequest);
            categoryJpaRepository.save(category);

            ItemRequest itemRequest = new ItemRequest(
                    "AirForce",
                    "AirForce Description",
                    SaleStatus.ON_SALE,
                    Money.of(100000),
                    LocalDateTime.now()
            );
            OptionRequest optionRequest = new OptionRequest(
                    "White240",
                    Money.of(1000)
            );
            Item item = Item.fromDto(itemRequest, category);
            itemJpaRepository.save(item);
            Option option = Option.fromDto(optionRequest);
            optionJpaRepository.save(option);
            product = new Product(item, option);
            productJpaRepository.save(product);
        } else {
            product = productJpaRepository.findAll().get(0);
        }

        // 재고 시딩
        List<Product> products = productJpaRepository.findAll();
        for (Product prod : products) {
            if (inventoryJpaRepository.findByProductId(prod.getId()).isEmpty()) {
                Inventory inventory = Inventory.builder()
                        .productId(prod.getId())
                        .quantity(10000)
                        .build();
                inventoryJpaRepository.save(inventory);
            }
        }

        // 주문 시딩
        if (orderJpaRepository.count() == 0) {
            Item item = itemJpaRepository.findById(product.getItem().getId())
                    .orElseThrow(() -> new IllegalStateException("Item not found for product id: " + product.getId()));
            int quantity = 2;
            Money unitPoint = item.getBasePrice();
            OrderProduct orderProduct = OrderProduct.builder()
                    .productId(product.getId())
                    .quantity(quantity)
                    .unitPoint(unitPoint)
                    .build();
            Money totalPoint = unitPoint.multiply(quantity);
            String orderNumber = "ORD-" + System.currentTimeMillis();
            Order order = new Order(managedUser, orderNumber, totalPoint, OrderStatus.CREATED);
            order.addOrderProduct(orderProduct);
            orderJpaRepository.save(order);
        }

        // 장바구니 시딩
        if (cartRepository.count() == 0) {
            Cart cart = new Cart(managedUser.getId());
            CartItem cartItem = new CartItem(product, 3, Money.of(10000));
            cart.addItemInCart(cartItem);
            cartRepository.save(cart);
        }
    }
}

