package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.BaseEntity;
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
import org.hibernate.StatelessSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//@Configuration
//@Profile({"local", "application-test"})
public class BulkDataLoaderAll {

    private static final int TOTAL_CATEGORIES = 100;
    private static final int TOTAL_USERS      = 1_000_000;
    private static final int TOTAL_OPTIONS    = 500;
    private static final int TOTAL_ITEMS      = 500_000;
    private static final int TOTAL_PRODUCTS   = 500_000;
    private static final int TOTAL_ORDERS     = 500_000;
    private static final int BATCH_SIZE       = 5_000;

    @Bean
    public CommandLineRunner loadAllDummyData(StatelessSession session) {
        return args -> {
            System.out.println(">>> BulkDataLoaderAll 시작!");
            session.beginTransaction();

            // 1) Category
            for (int i = 1; i <= TOTAL_CATEGORIES; i++) {
                Category cat = initTimestamps(
                        Category.builder()
                                .name("Category-" + i)
                                .build()
                );
                session.insert(cat);
                batchFlush(session, i);
            }
            session.getTransaction().commit();
            session.beginTransaction();

            // 2) User + UserPoint
            for (int i = 1; i <= TOTAL_USERS; i++) {
                User user = initTimestamps(
                        User.builder()
                                .username("user" + i)
                                .build()
                );
                Serializable userId = (Serializable) session.insert(user);

                UserPoint up = initTimestamps(
                        UserPoint.builder()
                                .user(User.builder().id((Long) userId).build())
                                .pointBalance(Money.of(ThreadLocalRandom.current().nextInt(0, 10_000)))
                                .build()
                );
                session.insert(up);
                batchFlush(session, i);
            }
            session.getTransaction().commit();
            session.beginTransaction();

            // 3) Option
            for (int i = 1; i <= TOTAL_OPTIONS; i++) {
                Option opt = initTimestamps(
                        Option.builder()
                                .name("Option-" + i)
                                .additionalCost(Money.of(ThreadLocalRandom.current().nextInt(100, 5_000)))
                                .build()
                );
                session.insert(opt);
                batchFlush(session, i);
            }
            session.getTransaction().commit();
            session.beginTransaction();

            // 4) Item
            for (int i = 1; i <= TOTAL_ITEMS; i++) {
                long refCatId = ThreadLocalRandom.current().nextLong(1, TOTAL_CATEGORIES + 1);
                Item item = initTimestamps(
                        Item.builder()
                                .name("Item-" + i)
                                .description("Description for item " + i)
                                .saleStatus(SaleStatus.ON_SALE)
                                .basePrice(Money.of(ThreadLocalRandom.current().nextInt(1_000, 100_000)))
                                .category(Category.builder().id(refCatId).build())
                                .build()
                );
                Serializable itemId = (Serializable) session.insert(item);
                batchFlush(session, i);
            }
            session.getTransaction().commit();
            session.beginTransaction();

            // 5) Product + Inventory
            for (int i = 1; i <= TOTAL_PRODUCTS; i++) {
                long refItemId   = ThreadLocalRandom.current().nextLong(1, TOTAL_ITEMS + 1);
                long refOptionId = ThreadLocalRandom.current().nextLong(1, TOTAL_OPTIONS + 1);

                Product prod = initTimestamps(
                        Product.builder()
                                .item(Item.builder().id(refItemId).build())
                                .option(Option.builder().id(refOptionId).build())
                                .build()
                );
                Serializable prodId = (Serializable) session.insert(prod);

                // Inventory: product 당 하나씩
                Inventory inv = initTimestamps(
                        Inventory.builder()
                                .productId((Long) prodId)
                                .quantity(ThreadLocalRandom.current().nextInt(1, 100))
                                .build()
                );
                session.insert(inv);

                batchFlush(session, i);
            }
            session.getTransaction().commit();
            session.beginTransaction();

            // 6) Order + OrderProduct
            for (int i = 1; i <= TOTAL_ORDERS; i++) {
                long refUserId = ThreadLocalRandom.current().nextLong(1, TOTAL_USERS + 1);
                long refProdId = ThreadLocalRandom.current().nextLong(1, TOTAL_PRODUCTS + 1);
                int qty         = ThreadLocalRandom.current().nextInt(1, 5);
                int unitPoint   = ThreadLocalRandom.current().nextInt(100, 10_000);

                Order order = initTimestamps(
                        Order.builder()
                                .userId(refUserId)
                                .orderNumber("ORD-" + UUID.randomUUID() + "-" + i)
                                .totalPoint(Money.of(unitPoint*qty))
                                .status(OrderStatus.CREATED)
                                .build()
                );
                Serializable orderId = (Serializable) session.insert(order);

                OrderProduct op = initTimestamps(
                        OrderProduct.builder()
                                .order(Order.builder().id((Long) orderId).build())
                                .productId(refProdId)
                                .quantity(qty)
                                .unitPoint(Money.of(unitPoint))
                                .build()
                );
                session.insert(op);
                batchFlush(session, i);
            }
            session.getTransaction().commit();
            session.close();
        };
    }

    private void batchFlush(StatelessSession session, int count) {
        if (count > 0 && count % BATCH_SIZE == 0) {
            session.getTransaction().commit();
            session.beginTransaction();
            System.out.println(">> flushed at " + count);
        }
    }

    private static <T extends BaseEntity> T initTimestamps(T entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }
}