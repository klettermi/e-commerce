package kr.hhplus.be.server.batch;

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
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class TestDataSeedTasklet implements Tasklet {

    private final UserJpaRepository userRepo;
    private final UserPointJpaRepository pointRepo;
    private final CategoryJpaRepository categoryRepo;
    private final ItemJpaRepository itemRepo;
    private final OptionJpaRepository optionRepo;
    private final ProductJpaRepository productRepo;
    private final InventoryJpaRepository inventoryRepo;
    private final OrderJpaRepository orderRepo;
    private final CartJpaRepository cartRepo;

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("✅ Batch Tasklet: Test Data 시딩 시작");

        // — User & Point
        User user;
        if (userRepo.count() == 0) {
            user = userRepo.saveAndFlush(User.builder().username("defaultUser").build());
            UserPoint up = new UserPoint();
            up.setUser(user);
            up.chargePoints(Money.of(100000));
            pointRepo.save(up);
        } else {
            user = userRepo.findAll().get(0);
        }

        // — Category, Item, Option, Product
        Product product;
        if (productRepo.count() == 0) {
            Category cat = Category.fromDto(new CategoryRequest("일반"));
            categoryRepo.save(cat);

            Item item = Item.fromDto(new ItemRequest(
                    "AirForce",
                    "AirForce Description",
                    SaleStatus.ON_SALE,
                    Money.of(100000),
                    LocalDateTime.now()
            ), cat);
            itemRepo.save(item);

            Option opt = Option.fromDto(new OptionRequest("White240", Money.of(1000)));
            optionRepo.save(opt);

            product = new Product(item, opt);
            productRepo.save(product);
        } else {
            product = productRepo.findAll().get(0);
        }

        // — Inventory
        if (inventoryRepo.findByProductId(product.getId()).isEmpty()) {
            inventoryRepo.save(Inventory.builder()
                    .productId(product.getId())
                    .quantity(10000)
                    .build()
            );
        }

        // — Order
        if (orderRepo.count() == 0) {
            Item item = itemRepo.findById(product.getItem().getId())
                    .orElseThrow();
            int qty = 2;
            Money unitPt = item.getBasePrice();
            Order order = new Order(user,
                    "ORD-" + System.currentTimeMillis(),
                    unitPt.multiply(qty),
                    OrderStatus.CREATED);
            order.addOrderProduct(OrderProduct.builder()
                    .productId(product.getId())
                    .quantity(qty)
                    .unitPoint(unitPt)
                    .build()
            );
            orderRepo.save(order);
        }

        // — Cart
        if (cartRepo.count() == 0) {
            Cart cart = new Cart(user.getId());
            cart.addItemInCart(new CartItem(product, 3, Money.of(10000)));
            cartRepo.save(cart);
        }

        log.info("✅ Batch Tasklet: Test Data 시딩 완료");
        return RepeatStatus.FINISHED;
    }
}
