package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.category.CategoryRepository;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.inventory.InventoryRepository;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.option.OptionRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@TestConfiguration
public class TestDataConfig {

    @Bean
    @Transactional
    public ApplicationRunner initTestData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ItemRepository itemRepository,
            OptionRepository optionRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            CouponRepository couponRepository,
            PointRepository pointRepository,
            OrderRepository orderRepository
    ) {
        return args -> {
            // 1) 사용자 생성
            User user = userRepository.save(
                    User.builder()
                            .username("testuser")
                            .build()
            );

            // 2) 포인트 계정 생성
            UserPoint point = new UserPoint();
            point.setUser(user);
            point.setPointBalance(Money.of(10000));
            pointRepository.save(point);

            // 3) 카테고리 생성
            Category electronics = categoryRepository.save(
                    Category.builder().name("Electronics").build()
            );
            Category books = categoryRepository.save(
                    Category.builder().name("Books").build()
            );

            // 4) 아이템 생성
            Item item1 = itemRepository.save(
                    Item.builder()
                            .name("Gaming Laptop")
                            .category(electronics)
                            .basePrice(Money.of(2000000))
                            .build()
            );
            Item item2 = itemRepository.save(
                    Item.builder()
                            .name("Backend Development Book")
                            .category(books)
                            .basePrice(Money.of(45000))
                            .build()
            );

            // 5) 옵션 생성
            Option opt1 = optionRepository.save(
                    Option.builder()
                            .name("16GB RAM Upgrade")
                            .additionalCost(Money.of(200000))
                            .build()
            );
            Option opt2 = optionRepository.save(
                    Option.builder()
                            .name("Hardcover Edition")
                            .additionalCost(Money.of(5000))
                            .build()
            );

            // 6) 상품 생성 (Item + Option)
            Product product1 = productRepository.save(
                    Product.builder()
                            .item(item1)
                            .option(opt1)
                            .build()
            );
            Product product2 = productRepository.save(
                    Product.builder()
                            .item(item2)
                            .option(opt2)
                            .build()
            );

            // 7) 재고 생성
            inventoryRepository.save(
                    Inventory.builder()
                            .productId(product1.getId())
                            .quantity(50)
                            .build()
            );
            inventoryRepository.save(
                    Inventory.builder()
                            .productId(product2.getId())
                            .quantity(30)
                            .build()
            );

            // 8) 쿠폰 생성 (정액 1,000원 할인)
            Coupon coupon = couponRepository.save(
                    Coupon.builder()
                            .name("TEST1000")
                            .couponCode("TEST1000")
                            .couponType(CouponType.AMOUNT)
                            .discountAmount(Money.of(1000))
                            .discountRate(null)
                            .totalQuantity(10)
                            .remainingQuantity(10)
                            .build()
            );

            // 9) 주문 생성 (상품가격 + 옵션가격 - 쿠폰할인)
            Money basePrice = product1.getItem().getBasePrice().add(product1.getOption().getAdditionalCost());
            Money discount = coupon.calculateDiscount(basePrice);
            Money totalPoint = basePrice.subtract(discount);
            Order order = orderRepository.save(
                    Order.builder()
                            .userId(user.getId())
                            .totalPoint(totalPoint)
                            .build()
            );

            // 10) 주문 상품 매핑
            orderRepository.saveOrderProduct(
                    OrderProduct.builder()
                            .order(order)
                            .productId(product1.getId())
                            .quantity(1)
                            .unitPoint(product1.getItem().getBasePrice())
                            .build()
            );
        };
    }
}