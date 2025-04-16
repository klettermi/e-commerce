package kr.hhplus.be.server;

import kr.hhplus.be.server.domain.cart.Cart;
import kr.hhplus.be.server.domain.cart.CartItem;
import kr.hhplus.be.server.domain.cart.CartRepository;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.category.CategoryRepository;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.option.OptionRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.point.UserPointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.category.dto.CategoryDto;
import kr.hhplus.be.server.interfaces.api.item.dto.ItemDto;
import kr.hhplus.be.server.interfaces.api.option.dto.OptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class TestDataSeeder {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;

    @Transactional  // 트랜잭션 보장
    public void testSeedData() {

        log.info("✅ Test Data Seeder 실행됨");

        // 사용자 및 UserPoint 시딩
        User managedUser;
        if (userRepository.count() == 0) {
            User user = User.builder()
                    .username("defaultUser")
                    .build();
            managedUser = userRepository.saveAndFlush(user);

            UserPoint userPoint = new UserPoint();
            userPoint.setUser(managedUser);
            userPoint.chargePoints(new Money(BigDecimal.valueOf(100000)));
            userPointRepository.save(userPoint);
        } else {
            managedUser = userRepository.findAll().get(0);
        }

        // 상품 시딩
        Product product;
        if (productRepository.count() == 0) {
            CategoryDto categoryDto = new CategoryDto("일반");
            Category category = Category.fromDto(categoryDto);
            categoryRepository.save(category);

            ItemDto itemDto = new ItemDto(
                    "AirForce",
                    "AirForce Description",
                    SaleStatus.ON_SALE,
                    new Money(BigDecimal.valueOf(100000)),
                    LocalDateTime.now()
            );
            OptionDto optionDto = new OptionDto(
                    "White240",
                    new Money(BigDecimal.valueOf(1000))
            );
            Item item = Item.fromDto(itemDto, category);
            itemRepository.save(item);
            Option option = Option.fromDto(optionDto);
            optionRepository.save(option);
            product = new Product(item, option);
            productRepository.save(product);
        } else {
            product = productRepository.findAll().get(0);
        }

        // 재고 시딩
        List<Product> products = productRepository.findAll();
        for (Product prod : products) {
            if (inventoryRepository.findByProductId(prod.getId()).isEmpty()) {
                Inventory inventory = Inventory.builder()
                        .productId(prod.getId())
                        .quantity(10000)
                        .build();
                inventoryRepository.save(inventory);
            }
        }

        // 주문 시딩
        if (orderRepository.count() == 0) {
            Item item = itemRepository.findById(product.getItem().getId())
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
            orderRepository.save(order);
        }

        // 장바구니 시딩
        if (cartRepository.count() == 0) {
            Cart cart = new Cart(managedUser.getId());
            CartItem cartItem = new CartItem(product, 3, new Money(BigDecimal.valueOf(10000)));
            cart.addItemInCart(cartItem);
            cartRepository.save(cart);
        }
    }
}

