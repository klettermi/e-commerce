package kr.hhplus.be.server;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile({"local", "test"})
public class DataLoader {

    @Bean
    @Transactional
    public CommandLineRunner seedData(UserRepository userRepository,
                                      UserPointRepository userPointRepository,
                                      ProductRepository productRepository,
                                      ItemRepository itemRepository,
                                      OptionRepository optionRepository,
                                      CategoryRepository categoryRepository,
                                      OrderRepository orderRepository,
                                      InventoryRepository inventoryRepository) {
        return args -> {

            // 사용자 및 UserPoint 시딩
            User managedUser;
            if (userRepository.count() == 0) {
                User user = new User();
                managedUser = userRepository.saveAndFlush(user);

                // UserPoint 생성 시 반드시 관리되는 사용자와 연관관계를 설정합니다.
                UserPoint userPoint = new UserPoint();
                userPoint.setUser(managedUser);  // 여기서 연관관계 설정
                userPoint.chargePoints(100000);    // 초기 포인트 충전 (메서드를 통해 설정)
                userPointRepository.save(userPoint);
            } else {
                managedUser = userRepository.findAll().get(0);
            }

            // 상품 시딩 (단 하나의 상품만 생성)
            Product product;
            if (productRepository.count() == 0) {
                CategoryDto categoryDto = new CategoryDto("일반");
                Category category = Category.fromDto(categoryDto);
                categoryRepository.save(category);

                ItemDto itemDto = new ItemDto(
                        "AirForce",
                        "AirForce Description",
                        SaleStatus.ON_SALE,
                        100000,
                        LocalDateTime.now()
                );
                OptionDto optionDto = new OptionDto(
                        "White240",
                        5000
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

            // 재고 시딩: 모든 상품에 대해 Inventory 생성
            List<Product> products = productRepository.findAll();
            for (Product prod : products) {
                // 기존에 Inventory가 없으면 생성
                if (inventoryRepository.findByProductId(prod.getId()).isEmpty()) {
                    Inventory inventory = Inventory.builder()
                            .productId(prod.getId())
                            .quantity(10)
                            .build();
                    inventoryRepository.save(inventory);
                }
            }

            // 주문 시딩: 단 하나의 주문 생성 (테스트 시 사용)
            if (orderRepository.count() == 0) {
                Item item = itemRepository.findById(product.getItem().getId())
                        .orElseThrow(() -> new IllegalStateException("Item not found for product id: " + product.getId()));

                int quantity = 2;
                int unitPoint = item.getBasePrice();
                OrderProduct orderProduct = OrderProduct.builder()
                        .productId(product.getId())
                        .quantity(quantity)
                        .unitPoint(unitPoint)
                        .build();

                int total = unitPoint * quantity;
                Money totalPoint = new Money(BigDecimal.valueOf(total));
                String orderNumber = "ORD-" + System.currentTimeMillis();
                Order order = new Order(managedUser, orderNumber, totalPoint, OrderStatus.CREATED);
                order.addOrderProduct(orderProduct);
                orderRepository.save(order);
            }
        };
    }
}
